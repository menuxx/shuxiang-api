import spark.Spark.*

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/8
 * 微信: yin80871901
 */

fun main(args: Array<String>) {

    threadPool(Config.ServerThreadPoolSize)

    port(Config.Port)

    initExceptionHandler { e ->
        e.printStackTrace()
    }

    init()

}