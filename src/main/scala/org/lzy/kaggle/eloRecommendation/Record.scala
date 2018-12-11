package org.lzy.kaggle.eloRecommendation

case class Record (

                    card_id: String ,
                    first_active_month: String ,
                    feature_1: Int ,
                    feature_2: Int ,
                    feature_3: Int ,
                    target: Double ,
                    new_installments_sum_stddev: Double ,
                    new_installments_sum_avg: Double ,
                    new_installments_min_stddev: Double ,
                    new_installments_min_avg: Double ,
                    new_installments_max_stddev: Double ,
                    new_installments_max_avg: Double ,
                    new_installments_std_stddev: Double ,
                    new_installments_std_avg: Double ,
                    new_purchase_amount_sum_stddev: Double ,
                    new_purchase_amount_sum_avg: Double ,
                    new_purchase_amount_min_stddev: Double ,
                    new_purchase_amount_min_avg: Double ,
                    new_purchase_amount_max_stddev: Double ,
                    new_purchase_amount_max_avg: Double ,
                    new_purchase_amount_std_stddev: Double ,
                    new_purchase_amount_std_avg: Double ,
                    new_category_1_avg: Double ,
                    new_category_1_std: Double ,
                    new_category_2_avg: Double ,
                    new_category_2_std: Double ,
                    new_city_id_count: Long ,
                    new_state_id_count: Long ,
                    new_subsector_id_count: Long ,
                    new_month_lag_avg: Double ,
                    auth_installments_sum_stddev: Double ,
                    auth_installments_sum_avg: Double ,
                    auth_installments_min_stddev: Double ,
                    auth_installments_min_avg: Double ,
                    auth_installments_max_stddev: Double ,
                    auth_installments_max_avg: Double ,
                    auth_installments_std_stddev: Double ,
                    auth_installments_std_avg: Double ,
                    auth_purchase_amount_sum_stddev: Double ,
                    auth_purchase_amount_sum_avg: Double ,
                    auth_purchase_amount_min_stddev: Double ,
                    auth_purchase_amount_min_avg: Double ,
                    auth_purchase_amount_max_stddev: Double ,
                    auth_purchase_amount_max_avg: Double ,
                    auth_purchase_amount_std_stddev: Double ,
                    auth_purchase_amount_std_avg: Double ,
                    auth_category_1_avg: Double ,
                    auth_category_1_std: Double ,
                    auth_category_2_avg: Double ,
                    auth_category_2_std: Double ,
                    auth_city_id_count: Long ,
                    auth_state_id_count: Long ,
                    auth_subsector_id_count: Long ,
                    auth_month_lag_avg: Double ,
                    hist_installments_sum_stddev: Double ,
                    hist_installments_sum_avg: Double ,
                    hist_installments_min_stddev: Double ,
                    hist_installments_min_avg: Double ,
                    hist_installments_max_stddev: Double ,
                    hist_installments_max_avg: Double ,
                    hist_installments_std_stddev: Double ,
                    hist_installments_std_avg: Double ,
                    hist_purchase_amount_sum_stddev: Double ,
                    hist_purchase_amount_sum_avg: Double ,
                    hist_purchase_amount_min_stddev: Double ,
                    hist_purchase_amount_min_avg: Double ,
                    hist_purchase_amount_max_stddev: Double ,
                    hist_purchase_amount_max_avg: Double ,
                    hist_purchase_amount_std_stddev: Double ,
                    hist_purchase_amount_std_avg: Double ,
                    hist_category_1_avg: Double ,
                    hist_category_1_std: Double ,
                    hist_category_2_avg: Double ,
                    hist_category_2_std: Double ,
                    hist_city_id_count: Long ,
                    hist_state_id_count: Long ,
                    hist_subsector_id_count: Long ,
                    hist_month_lag_avg: Double
                  )
