<template>
    <el-dialog title="添加群聊" width="30%"
               :visible.sync="$store.state.group.addGroupDialog"
               :before-close="handleClose">
        <el-form label-position="right" label-width="90px" :inline="true">
            <el-form-item label="搜索群名称">
                <el-input v-model="addGroupName" autocomplete="off" style="width: 320px;"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button icon="el-icon-search" circle @click="searchGroup"></el-button>
            </el-form-item>
        </el-form>

        <!-- 展示群聊搜索结果 -->
        <div class="user-list" v-show="searchGroupList.length">
            <div class="title">
                搜索结果
            </div>
            <div class="user-item" v-for="(item, index) in searchGroupList" :key="index">
                <div class="user-img"><img :src="item.avatar" alt="NO IMG"></div>
                <div class="info">
                    <div class="nick">{{ item.name }}</div>
                </div>
                <div class="add-btn">
                    <el-button type="success" @click="addGroup(item.id)">申请加群</el-button>
                </div>
            </div>
        </div>

        <!-- 显示添加群聊信息 -->
        <div class="add-friend" v-show="addGroupFlag">
            <el-form ref="form" label-width="120px">
                <el-form-item label="请填写验证信息">
                    <el-input v-model="confirmInfo"></el-input>
                </el-form-item>
                <el-form-item size="large">
                    <el-button type="primary" @click="confirmAddGroup">确认申请</el-button>
                    <el-button @click="cancelAddGroup">取消</el-button>
                </el-form-item>
            </el-form>
        </div>
    </el-dialog>
</template>

<script>
    import groupApi from "../../api/group";
    import {getUser} from "../../utils/auth";

    export default {
        data() {
            return {
                addGroupName: "",
                searchGroupList: [],
                addGroupFlag: false,
                confirmInfo: "大家好，我是" + getUser().nick,
                addGroupId: 0,
            };
        },
        methods: {
            // 搜索群
            searchGroup() {
                if (this.addGroupName.trim() === "") {
                    this.$message({
                        message: '群名称不能为空',
                        type: 'info'
                    });
                    return;
                }
                // 搜索群聊
                groupApi.searchGroupByName(this.addGroupName).then((response) => {
                    if (response.data.flag && response.data.data.length !== 0) {
                        this.searchGroupList = response.data.data;
                        this.addGroupFlag = false;
                    } else {
                        this.$message({
                            message: '没有该群聊',
                            type: 'error'
                        });
                    }
                });
            },
            // 添加群
            addGroup(groupId) {
                this.searchGroupList = [];     // 隐藏群聊搜索内容
                this.addGroupFlag = true;   // 显示添加群组
                this.addGroupId = groupId;
            },
            // 填写完信息，确认添加群
            confirmAddGroup() {
                if (this.confirmInfo.trim() === "") {
                    this.$message({
                        message: '请填写验证信息',
                        type: 'info'
                    });
                    return;
                }
                // 进行添加
                groupApi.addGroup(getUser().id, this.addGroupId, this.confirmInfo).then((response) => {
                    this.$notify({
                        title: (response.data.flag ? "成功" : "失败"),
                        message: (response.data.flag ? "添加成功，请等待审核通过。" : response.data.message),
                        type: (response.data.flag ? "success" : "error"),
                    });
                    if (response.data.flag) {
                        this.cancelAddGroup();
                    }
                });
            },
            // 取消添加群
            cancelAddGroup() {
                this.$store.dispatch("group/setaddGroupDialog", false);
                this.addGroupName = "";
                this.searchGroupList = [];
                this.addGroupFlag = false;
                this.confirmInfo = "大家好，我是" + getUser().nick;
                this.addGroupId = 0;
            },
            handleClose() {
                this.cancelAddGroup();
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
