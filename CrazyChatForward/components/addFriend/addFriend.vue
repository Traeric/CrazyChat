<template>
    <el-dialog title="添加好友" width="30%"
               :visible.sync="$store.state.friend.addFriendDialog"
               :before-close="handleClose">
        <el-form label-position="right" label-width="80px" :inline="true">
            <el-form-item label="搜索用户">
                <el-input v-model="addFriendName" autocomplete="off" style="width: 320px;"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" icon="el-icon-search" circle @click="searchFriend"></el-button>
            </el-form-item>
        </el-form>

        <div class="user-list" v-show="searchUser.length !== 0">
            <div class="title">
                搜索结果
            </div>
            <div class="user-item" v-for="(item, index) in searchUser" :key="index">
                <div class="user-img"><img :src="item.avatar" alt="NO IMG"></div>
                <div class="info">
                    <div class="nick">{{ item.name }}</div>
                    {{ item.sign }}
                </div>
                <div class="add-btn">
                    <el-button type="success" @click="addFriend(item.id)">添加好友</el-button>
                </div>
            </div>
        </div>

        <div class="add-friend" v-show="addFriendFlag">
            <el-form ref="form" label-width="120px">
                <el-form-item label="请填写验证信息">
                    <el-input v-model="addFriendMap.confirmInfo"></el-input>
                </el-form-item>
                <el-form-item label="备注">
                    <el-input v-model="addFriendMap.todo"></el-input>
                </el-form-item>
                <el-form-item label="分组">
                    <el-select v-model="addFriendMap.group">
                        <el-option v-for="(item, index) in groupArray" :key="index"
                                   :label="item.name" :value="item.id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item size="large">
                    <el-button type="primary" @click="confirmAddFriend(addFriendId)">确认添加</el-button>
                    <el-button @click="cancelAddFriend">取消</el-button>
                </el-form-item>
            </el-form>
        </div>
    </el-dialog>
</template>

<script>
    import userApi from "../../api/user";
    import friendApi from "../../api/friend";
    import {getUser} from "../../utils/auth";

    export default {
        data() {
            return {
                addFriendName: "",
                searchUser: [],
                addFriendMap: {
                    confirmInfo: "",
                    todo: "",
                    group: "",
                },
                addFriendFlag: false,
                groupArray: [],
                addFriendId: 0,
            };
        },
        methods: {
            // 搜索用户
            searchFriend() {
                if (this.addFriendName.trim() === "") {
                    this.$message({
                        message: '用户名不能为空',
                        type: 'info'
                    });
                    return;
                }
                // 进行搜索
                userApi.searchUser(this.addFriendName).then((response) => {
                    if (response.data.flag && response.data.data.length !== 0) {
                        this.searchUser = response.data.data;
                        this.addFriendFlag = false;
                    } else {
                        this.$message({
                            message: '没有该用户',
                            type: 'error'
                        });
                    }
                });
            },
            // 添加好友
            addFriend(friendId) {
                // 设置默认备注跟默认验证信息
                this.addFriendMap.confirmInfo = "你好，我是" + getUser().nick;
                this.addFriendMap.todo = this.addFriendName;
                // 获取用户的分组
                userApi.getGroupList(getUser().id).then((response) => {
                    this.searchUser = [];     // 隐藏用户搜索内容
                    this.addFriendFlag = true;   // 显示添加用户

                    this.groupArray = response.data.data;
                    this.addFriendMap.group = response.data.data[0].id;
                });
                this.addFriendId = friendId;
            },
            // 确认添加好友
            confirmAddFriend(friendId) {
                if (this.addFriendMap.todo.trim() === "") {
                    this.$message({
                        message: '请设置用户备注',
                        type: 'info'
                    });
                    return;
                }
                // 进行添加
                friendApi.addFriend(getUser().id, friendId, this.addFriendMap).then((response) => {
                    this.$notify({
                        title: (response.data.flag ? "成功" : "失败"),
                        message: (response.data.flag ? response.data.message + "，请等待对方同意。" : response.data.message),
                        type: (response.data.flag ? "success" : "error"),
                    });
                    if (response.data.flag) {
                        this.cancelAddFriend();
                    }
                });
            },
            // 取消添加好友
            cancelAddFriend() {
                this.$store.dispatch("friend/setaddFriendDialog", false);
                this.addFriendName = "";
                this.searchUser = [];
                this.addFriendFlag = false;
                this.addFriendId = 0;
            },
            // 关闭窗口
            handleClose() {
                this.cancelAddFriend();
            },
        },
    }
</script>

<style scoped lang="stylus">
    .user-list
        margin 0 auto
        width 70%

        .title
            margin-bottom 5px
            font-weight 700
            font-size 20px

        .user-item
            display flex
            margin-bottom 10px
            padding 5px
            cursor pointer
            border 1px solid #dedede
            border-radius 4px
            box-shadow 2px 2px 6px #c8c8c8

            &:hover
                background-color #f6f6f6

        .user-img
            flex 0 0 46px
            width 46px
            height 46px

            img
                margin 3px
                width 40px
                height 40px
                border-radius 12px

        .info
            flex 1
            padding 0 10px
            width 0
            white-space nowrap
            text-overflow ellipsis
            overflow hidden

        .add-btn
            flex 0 0 60px
            margin-top 3px


    .add-friend
        padding 10px 20px
        border-radius 8px
        box-shadow 2px 2px 6px #c8c8c8
        border 1px solid #dedede
</style>
