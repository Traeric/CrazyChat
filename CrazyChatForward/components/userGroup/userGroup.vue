<template>
    <el-dialog title="分组管理" :visible.sync="$store.state.user.userGroupDialog"
               :before-close="handleClose" width="20%">
        <div class="group-item" v-for="(item, index) in $store.state.user.userGroup" :key="index">
            {{ item.name }}
            <span class="delete" v-show="deleteIcon" @click="deleteGroup($event, item.id)"><i class="el-icon-remove"></i></span>
        </div>
        <div slot="footer" class="dialog-footer">
            <el-button type="success" @click="addUserGroup">新增分组</el-button>
            <el-button type="danger" @click="deleteIcon = !deleteIcon">管 理</el-button>
        </div>
    </el-dialog>
</template>

<script>
    import userApi from "../../api/user";
    import {getUser} from "../../utils/auth";

    export default {
        data() {
            return {
                deleteIcon: false,
            };
        },
        methods: {
            handleClose() {
                this.$store.dispatch("user/setUserGroupDialog", false);
            },
            // 删除分组
            deleteGroup(event, group_id) {
                let currentDom = event.currentTarget;
                this.$confirm('此操作将永久删除该分组, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'error'
                }).then(() => {
                    // 查询该分组中还有没有好友
                    userApi.groupIsEmpty(group_id).then((response) => {
                        if (response.data.data) {
                            // 没有好友，删除
                            userApi.deleteGroup(group_id).then((response) => {
                                this.$notify({
                                    title: (response.data.flag ? "成功" : "失败"),
                                    message: response.data.message,
                                    type: (response.data.flag ? "success" : "error"),
                                });
                                if (response.data.flag) {
                                    $(currentDom).parent().remove();
                                }
                            });
                        } else {
                            // 有好友
                            this.$notify({
                                title: '分组不为空',
                                message: '该分组中仍存在好友，不能被删除，请将好友全部移出分组再进行删除。',
                                type: 'warning'
                            });
                        }
                    });
                });
            },
            // 新增分组
            addUserGroup() {
                // 弹框
                this.$prompt('请输入分组名称', '新建分组', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    inputPattern: /^[\u4e00-\u9fa5_a-zA-Z0-9]+$/,
                    inputErrorMessage: '分组名称格式不正确'
                }).then(({ value }) => {
                    userApi.addUserGroup(getUser().id, value).then((response) => {
                        this.$notify({
                            title: (response.data.flag ? "成功" : "失败"),
                            message: response.data.message,
                            type: (response.data.flag ? "success" : "error"),
                        });
                        if (response.data.flag) {
                            // 重新查询一遍分组
                            userApi.getUserGroup(getUser().id).then((response) => {
                                this.$store.dispatch("user/setUserGroup", response.data.data);
                            });
                        }
                    });
                });
            },
        },
    }
</script>

<style scoped lang="stylus">
    .group-item
        margin-left 30px
        padding 0 10px
        height 40px
        border-bottom 1px solid #dedede
        line-height 40px
        &:hover
            background-color #f2f2f2
        span
            float right
            cursor pointer
            i
                color #f00
                font-size 18px
</style>
