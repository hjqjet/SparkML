package org.lzy.kaggle.kaggleSantander

import common.{FeatureUtils, DataUtils}
import org.apache.spark.ml.{Pipeline, PipelineStage}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
/*
   spark-submit --master yarn-client --queue all --driver-memory 6g --conf spark.driver.maxResultSize=5g  \
   --num-executors 14 --executor-cores 4 --executor-memory 6g  \
   --class org.lzy.kaggle.kaggleSantander.Run SparkML.jar
 */
/**
  * Created by Administrator on 2018/7/3.
  *
  * Created by Administrator on 2018/6/3

  **/
object Run {
    def main(args: Array[String]): Unit = {
        val spark = SparkSession.builder().appName("names")
//            .master("local[*]")
                .getOrCreate()
        import spark.implicits._
        spark.sparkContext.setLogLevel("WARN")
        val conf = spark.conf
        val sc = spark.sparkContext
        sc.setCheckpointDir(Constant.basePath+"checkpoint")
        val config = sc.getConf
        //    config.set("spark.driver.maxResultSize","0")
        config.set("spark.debug.maxToStringFields", "100")
        config.set("spark.shuffle.io.maxRetries", "60")
        config.set("spark.default.parallelism", "54")
        config.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
        val utils = new DataUtils(spark)
//        val models = new Models(spark)
        val featureExact = new FeatureExact(spark)
//        val run = new Run(spark)
        val trainModel = new TrainModel(spark)
        val train_df = utils.read_csv(Constant.basePath + "AData/train.csv").repartition(100).cache()
        val test_df = utils.read_csv(Constant.basePath + "AData/test.csv").repartition(100).cache()
        /*
通过分桶+分类的方式来驯良并导出模型
 */
//        run.trainGBDTClassic(train_df,test_df)
        /*
        验证GBDT在不同参数情况下的分数
         */
//        run.evaluatorGBDT(train_df)
//        trainModel.testSelectorChiSq(train_df, 0,0.01)
//        trainModel.testChiSqByRFSelect(train_df,372)
//        trainModel.testChiSqByTopNum(train_df,372)
        /*
        训练GBDT并将数据导出
         */
//        trainModel.fitByGBDT(train_df, test_df, 0.01, 400)

        /*
        通过随机森林训练查看特征重要性。
         */
//        featureExact.selectFeaturesByRF(train_df)

//        trainModel.generateFromColumns(test_df)
//        trainModel.fitWithStatistic(train_df,test_df)
//        trainModel.evaluateWithStatistic(train_df,Array("sum", "mean", "std", "nans"))
//        Array("sum", "mean", "std", "nans", "median").foreach(column=>{
//            trainModel.evaluateWithStatistic(train_df,Array(column))
//        })

        /**
          * 泄漏相关代码测试
          */
//        featureExact.compiledLeadResult(train_df)
//       val (nonUgly_test_df, non_ugly_indexes, ugly_indexes)= featureExact.getBueautifulTest(train_df,test_df)
//        val nonUgly_test_df=spark.read.parquet(Constant.basePath+"cache/nonUgly_test_df")
//        val (test_leak,leaky_value_counts)=featureExact.compiledLeakResult_test(nonUgly_test_df,22)

      trainModel.lagSelectFakeRows(train_df,test_df)
spark.stop()
    }
}

class Run(spark: SparkSession) {

    import spark.implicits._


    /** *
      * 功能实现:
      * 通过多个变量值来检测使用GBDT得到的分数
      * Author: Lzy
      * Date: 2018/7/9 9:19
      * Param: [train_df]
      * Return: void
      */



    def evaluatorGBDT(train_df: DataFrame) = {
        val trainModel = new TrainModel(spark)

//        Array(0.01, 0.001, 0.003, 0.005).foreach(fdr => {
//            trainModel.testChiSqByFdr(train_df, fdr)
//        })
        Array(500, 1000, 2000, 3000).foreach(num => {
            trainModel.testSelectorChiSq(train_df, type_num = 0, num)
        })
    }
/***
 * 功能实现:
 *使用分桶+分类来训练和保存模型，并导出
 * Author: Lzy
 * Date: 2018/7/10 19:25
 * Param: [train_df, test_df]
 * Return: void
 */
    def trainGBDTClassic(train_df:DataFrame,test_df:DataFrame): Unit ={
        val trainModel = new TrainModel(spark)

        val all_df=FeatureUtils.concatTrainAndTest(train_df,test_df,Constant.lableCol)
        trainModel.fitByGBDTAndBucket(all_df,1000)
//        trainModel.transformAndExplot_GBDTBucket(test_df,Constant.basePath + "model/gbdt_classic_model")
    }


}
