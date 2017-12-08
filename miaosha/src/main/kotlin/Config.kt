/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/8
 * 微信: yin80871901
 */


object Config {

    // 生产环境配置为 500
    val ServerThreadPoolSize = 10

    val BindHost = System.getenv("PORT") ?: "127.0.0.1"
    val Port = Integer.parseInt(System.getenv("PORT")) ?: 8080

}