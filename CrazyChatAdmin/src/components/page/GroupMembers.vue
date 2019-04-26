<template>
    <div class="">
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item><i class="el-icon-lx-group"></i> 群成员</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <el-tabs v-model="message">
                <el-tab-pane :label="`群员列表(${members.length})`" name="first">
                    <el-table :data="members" :show-header="false" style="width: 100%" :highlight-current-row="true">
                        <el-table-column width="200">
                            <template slot-scope="scope">
                                <span class="message-title">{{scope.row.id}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column width="80">
                            <template slot-scope="scope">
                                <img :src="scope.row.avatar" alt="NO IMG" style="width: 60px; height: 60px; border-radius: 12px;">
                            </template>
                        </el-table-column>
                        <el-table-column prop="nick"></el-table-column>
                        <el-table-column width="120">
                            <template slot-scope="scope">
                                <el-tag size="medium" :type="'danger'" v-if="scope.row.type === '0'">
                                    群主
                                </el-tag>
                                <el-tag size="medium" :type="'success'" v-else>
                                    成员
                                </el-tag>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-tab-pane>
            </el-tabs>
        </div>
    </div>
</template>

<script>
    import groupApi from "../../api/group";

    export default {
        data() {
            return {
                message: 'first',
                showHeader: false,
                members: [],
            }
        },
        created() {
            // 获取群id
            const group_id = this.$route.params.group_id;
            // 检测昵称是否符合
            if (!/^[0-9]+$/g.test(group_id)) {
                this.$router.push("/404");
            }
            // 获取所有的成员
            groupApi.getAllMembers(group_id).then((response) => {
                this.members = response.data.data;
            });
        },
    }

</script>

<style>
    .message-title {
        cursor: pointer;
    }
</style>

