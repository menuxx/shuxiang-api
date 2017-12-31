import com.menuxx.Application
import com.menuxx.code.db.ItemCodeTaskDb
import com.menuxx.code.service.CodeBatchService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest(classes = [Application::class])
@RunWith(SpringRunner::class)
class ItemCodeTaskDbTest {

    @Autowired
    lateinit var taskDb : ItemCodeTaskDb

    @Autowired
    lateinit var batchService: CodeBatchService

    @Test
    fun test1() {
        //println(taskDb.tryUpdateFinish(7))
    }

    @Test
    fun test2() {
        //println(taskDb.getTaskByBatchId(17))
    }

    @Test
    fun calcBatchCountTest() {
        println(batchService.calcBatchCount(1001).size)
    }

    @Test
    fun doBatchPlanTest() {
        println(batchService.doBatchPlan(1001).size)
    }

}