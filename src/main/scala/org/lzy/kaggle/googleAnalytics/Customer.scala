package org.lzy.kaggle.googleAnalytics

class Customer {

  case class Customer(
                       //用户通过什么渠道进入商店，共8种，Organic站42%， social 25% 。。。
                       channelGrouping: String,
                       //访问商店的时间，20171016
                       date: String,
                       //访问商店的设备，是一个长json，共176中，最多的站31%
                       device: String,
                       //用户的唯一标识符
                       fullVisitorId: Double,
                       //用户的地理位置相关信息,是一个长json，最多的站7%
                       geoNetwork: String,
                       //访问商店行为的唯一识别符，有大约1k条的重复，所以不是唯一
                       sessionId: String,
                       //用户的参与类型，社交参与/非社交参与。Socially Engaged" or "Not Socially Engaged
                     //所有数据中都为非社交参与
                       socialEngagementType: String,
                       //总值，是一个json字符串，表示对一些特定值的访问次数，大约百分之40%的数据每一项访问了一次。
                     //	{"visits": "1", "hits": "4", "pageviews": "4"}
                       totals: String,
                       //会话流量来源的相关信息，json字符串，
                       // {"campaign": "(not set)", "source": "google", "medium": "organic", "keyword": "(not provided)", "adwordsClickInfo": {"criteriaParameters": "not available in demo dataset"}, "isTrueDirect": true}
                       trafficSource: String,
                       //会话的标志服，对用户而言是唯一的，全局唯一的应该使用fullVisitorId和visitId的组合
                       visitId: Int,
                       //会话的号码，如果是第一个会话，使用1，大部分为1，有极端值，最大395
                       visitNumber: String,
                       //时间戳，毫秒值
                       visitStartTime: Long

                     )

}
