
package org.lzy.transmogriAI

import com.salesforce.op._
import com.salesforce.op.evaluators.Evaluators
import com.salesforce.op.features.{Feature, FeatureBuilder, FeatureLike}
import com.salesforce.op.features.types._
import com.salesforce.op.readers.{CSVProductReader, DataReaders}
import com.salesforce.op.stages.impl.classification.BinaryClassificationModelSelector
import com.salesforce.op.stages.impl.classification.ClassificationModelsToTry._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 *针对csv数据格式创建对应的case class ，nullable字段必须使用option来替代
 * @param id       passenger id
 * @param survived 1: survived, 0: did not survive
 * @param pClass   passenger class
 * @param name     passenger name
 * @param sex      passenger sex (male/female)
 * @param age      passenger age (one person has a non-integer age so this must be a double)
 * @param sibSp    number of siblings/spouses traveling with this passenger
 * @param parCh    number of parents/children traveling with this passenger
 * @param ticket   ticket id string
 * @param fare     ticket price
 * @param cabin    cabin id string
 * @param embarked location where passenger embarked
 */
case class Passenger
(
  id: Int,
  survived: Int,
  pClass: Option[Int],
  name: Option[String],
  sex: Option[String],
  age: Option[Double],
  sibSp: Option[Int],
  parCh: Option[Int],
  ticket: Option[String],
  fare: Option[Double],
  cabin: Option[String],
  embarked: Option[String]
)

object OpTitanicSimple {

  /**
   * Run this from the command line with
   * ./gradlew sparkSubmit -Dmain=com.salesforce.hw.OpTitanicSimple -Dargs=/full/path/to/csv/file
   */
  def main(args: Array[String]): Unit = {
//    if (args.isEmpty) {
//      println("You need to pass in the CSV file path as an argument")
//      sys.exit(1)
//    }
//    val csvFilePath = args(0)
    val csvFilePath=
//System.load().getenv("TitanicDataset/TitanicPassengersTrainData.csv")
ClassLoader.getSystemResource("TitanicDataset/TitanicPassengersTrainData.csv").toString
    println(s"使用的csv数据为: $csvFilePath")

    // Set up a SparkSession as normal
    val conf = new SparkConf().setAppName(this.getClass.getSimpleName.stripSuffix("$"))
        .setMaster("local[*]")

    implicit val spark = SparkSession.builder.config(conf).getOrCreate()
spark.sparkContext.setLogLevel("warn")
    ////////////////////////////////////////////////////////////////////////////////
    // RAW FEATURE DEFINITIONS
    //      特征列定义
    /////////////////////////////////////////////////////////////////////////////////

      //基于数据类型使用op类型转换
    // Define features using the OP types based on the data
    val survived = FeatureBuilder.RealNN[Passenger].extract(_.survived.toRealNN).asResponse
    val pClass = FeatureBuilder.PickList[Passenger].extract(_.pClass.map(_.toString).toPickList).asPredictor
    val name = FeatureBuilder.Text[Passenger].extract(_.name.toText).asPredictor
    val sex = FeatureBuilder.PickList[Passenger].extract(_.sex.map(_.toString).toPickList).asPredictor
    val age = FeatureBuilder.Real[Passenger].extract(_.age.toReal).asPredictor
    val sibSp = FeatureBuilder.Integral[Passenger].extract(_.sibSp.toIntegral).asPredictor
    val parCh = FeatureBuilder.Integral[Passenger].extract(_.parCh.toIntegral).asPredictor
    val ticket = FeatureBuilder.PickList[Passenger].extract(_.ticket.map(_.toString).toPickList).asPredictor
    val fare = FeatureBuilder.Real[Passenger].extract(_.fare.toReal).asPredictor
    val cabin = FeatureBuilder.PickList[Passenger].extract(_.cabin.map(_.toString).toPickList).asPredictor
    val embarked = FeatureBuilder.PickList[Passenger].extract(_.embarked.map(_.toString).toPickList).asPredictor

    ////////////////////////////////////////////////////////////////////////////////
    // TRANSFORMED FEATURES
      //特征修改
    /////////////////////////////////////////////////////////////////////////////////

    // Do some basic feature engineering using knowledge of the underlying dataset
      //使用已有认知对目前特征做一些基本特征工程，
    val familySize = sibSp + parCh + 1  //家庭人数，+1是本人
    val estimatedCostOfTickets = familySize * fare //总票价
    val pivotedSex = sex.pivot() //行转列
    val normedAge = age.fillMissingWithMean().zNormalize() //使用平均值进行填充年龄，并进行标准化
    val ageGroup = age.map[PickList](_.value.map(v => if (v > 18) "adult" else "child").toPickList) //区分是孩子还是成人

    // Define a feature of type vector containing all the predictors you'd like to use
      //创建特征向量
    val passengerFeatures:FeatureLike[OPVector] = Seq(
      pClass, name, age, sibSp, parCh, ticket,
      cabin, embarked, familySize, estimatedCostOfTickets,
      pivotedSex, ageGroup
    ).transmogrify()

    // Optionally check the features with a sanity checker
      //数据泄露检测
    val sanityCheck = true
    val finalFeatures = if (sanityCheck) survived.sanityCheck(passengerFeatures) else passengerFeatures

    // Define the model we want to use (here a simple logistic regression) and get the resulting output
      //自定义希望使用的模型
    val (prediction, rawPrediction, prob):(FeatureLike[RealNN], FeatureLike[OPVector], FeatureLike[OPVector]) =
      BinaryClassificationModelSelector.withTrainValidationSplit()
        .setModelsToTry(LogisticRegression)
        .setInput(survived, finalFeatures).getOutput()

    val evaluator = Evaluators.BinaryClassification()
      .setLabelCol(survived)
      .setRawPredictionCol(rawPrediction)
      .setPredictionCol(prediction)
      .setProbabilityCol(prob)

    ////////////////////////////////////////////////////////////////////////////////
    // WORKFLOW
    /////////////////////////////////////////////////////////////////////////////////

    import spark.implicits._ // Needed for Encoders for the Passenger case class
    // Define a way to read data into our Passenger class from our CSV file
    //自定义读取数据方式，按照id来
    val trainDataReader:CSVProductReader[Passenger] = DataReaders.Simple.csvCase[Passenger](
      path = Option(csvFilePath),
      key = _.id.toString
    )

    // Define a new workflow and attach our data reader
    val workflow =      new OpWorkflow()
        .setResultFeatures(survived, rawPrediction, prob, prediction)
        .setReader(trainDataReader)

    // Fit the workflow to the data
    val fittedWorkflow = workflow.train()
    println(s"Summary: ${fittedWorkflow.summary()}")

    // Manifest the result features of the workflow
    println("Scoring the model")
    val (dataframe, metrics) = fittedWorkflow.scoreAndEvaluate(evaluator = evaluator)

    println("Transformed dataframe columns:")
    dataframe.columns.foreach(println)
    println("Metrics:")
    println(metrics)
  }
}
