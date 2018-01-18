package com.menuxx.xuerengroup.ctrl

import com.menuxx.*
import com.menuxx.sso.bean.ApiRespWithData
import com.menuxx.code.mongo.ItemCodeRepository
import com.menuxx.code.parseUrlPathCode
import com.menuxx.common.bean.GroupTopic
import com.menuxx.common.db.GroupTopicDb
import com.menuxx.xuerengroup.service.GroupService
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/16
 * 微信: yin80871901
 */

@AllOpen
@RestController
@RequestMapping("/xr_groups")
class GroupCtrl (
        private val groupTopicDb: GroupTopicDb,
        private val groupService: GroupService,
        private val codeRepository: ItemCodeRepository
) {

    data class ItemCode(@NotEmpty val codeUrl: String)
    @PostMapping("/{groupId}/consume_code")
    fun consumeCodeToGroup(@PathVariable groupId: Int, @Valid @RequestBody itemCode: ItemCode) : ResponseEntity<ApiRespWithData<Int>> {
        val user = getCurrentUser()
        val data = parseUrlPathCode(itemCode.codeUrl)
        val itemCode = codeRepository.getItemCodeDataByCodeWithSalt(code = data.code, salt = data.salt)
        return if ( itemCode != null ) {
            val res = groupService.consumeItemCodeToGroup(itemCode = itemCode, userId = user.id, groupId = groupId, channel = data.channel)
            return if ( res == 2 ) {
                ResponseEntity.ok(ApiRespWithData(Const.NotErrorCode, "消费成功", 1))
            } else {
                ResponseEntity.ok(ApiRespWithData(Const.NotErrorCode, "消费失败，重复消费", 0))
            }
        } else {
            ResponseEntity.badRequest().body(ApiRespWithData(-1, "消费失败，或code不存在", 0))
        }
    }

    @GetMapping("/{groupId}/topics")
    fun getGroupTopic(@PathVariable groupId: Int, @RequestParam(defaultValue = Page.DefaultPageNumText) pageNum: Int, @RequestParam(defaultValue = Page.DefaultPageSizeText) pageSize: Int) : List<GroupTopic> {
        return groupTopicDb.getTopicsByGroupId(groupId, PageParam(pageNum, pageSize)).map {
            it.creator = removeSensitiveData(it.creator)
            it
        }
    }

    data class PostTopic(@NotEmpty val content: String)
    @PostMapping("/{groupId}/topics")
    fun createTopic(@PathVariable groupId: Int, @Valid @RequestBody post: PostTopic) : GroupTopic {
        val topic = GroupTopic()
        topic.content = post.content
        topic.creatorId = getCurrentUser().id
        return groupTopicDb.insertTopicToGroup(groupId, topic)
    }

}