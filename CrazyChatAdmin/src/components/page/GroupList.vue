<template>
    <div class="table">
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item><i class="el-icon-lx-group"></i> CrazyChat群聊列表</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <!-- 操作区域 -->
            <div class="handle-box">
                <el-button type="danger" icon="delete" class="handle-del mr10" @click="delAll">批量删除</el-button>
                <el-input v-model="groupName" placeholder="通过群聊筛选" class="handle-input mr10"></el-input>
                <el-button type="primary" icon="search" @click="search">搜索</el-button>
            </div>
            <!-- 数据区域 -->
            <el-table :data="tableData" border class="table" ref="multipleTable"
                      @selection-change="handleSelectionChange" :highlight-current-row="true">
                <el-table-column type="selection" width="55" align="center"></el-table-column>
                <el-table-column prop="id" label="群聊id" sortable align="center">
                </el-table-column>
                <el-table-column label="群聊名称" align="center">
                    <template slot-scope="scope">
                        <el-popover trigger="hover" placement="top">
                            <p>姓名: {{ scope.row.name }}</p>
                            <p>
                                <img style="width: 60px; height: 60px; border-radius: 12px;" :src="scope.row.picture"
                                     alt="NO IMG">
                            </p>
                            <div slot="reference" class="name-wrapper" style="cursor: pointer;">
                                <el-tag size="medium" :type="'success'">
                                    {{ scope.row.name }}
                                </el-tag>
                            </div>
                        </el-popover>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" align="center">
                </el-table-column>
                <el-table-column label="操作" width="180" align="center">
                    <template slot-scope="scope">
                        <router-link :to="{path:'/group_member/' + scope.row.id}" style="margin-right: 10px;">
                            <el-button type="text" icon="el-icon-lx-attention">
                                查看群聊成员
                            </el-button>
                        </router-link>
                        <el-button type="text" icon="el-icon-delete" class="red"
                                   @click="handleDelete(scope.row.id, scope.row)">
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pagination">
                <el-pagination background @current-change="handleCurrentChange" layout="prev, pager, next"
                               :total="totalSize">
                </el-pagination>
            </div>
        </div>
    </div>
</template>

<script>
    import groupApi from "../../api/group";

    export default {
        data() {
            return {
                tableData: [],
                cur_page: 1,
                totalSize: 0,
                multipleSelection: [],  // 所有被选中的列
                groupName: '',
            }
        },
        created() {
            groupApi.findAllGroup(1).then((response) => {
                this.tableData = response.data.data.rows;
                this.totalSize = response.data.data.allNum;
            });
        },
        methods: {
            // 分页
            handleCurrentChange(val) {
                this.cur_page = val;
                if (this.groupName) {
                    groupApi.searchByName(this.groupName, val).then((response) => {
                        this.tableData = response.data.data.rows;
                        this.totalSize = response.data.data.allNum;
                    });
                } else {
                    groupApi.findAllGroup(val).then((response) => {
                        this.tableData = response.data.data.rows;
                        this.totalSize = response.data.data.allNum;
                    });
                }
            },
            // 搜索群成员
            search() {
                if (!this.groupName) {
                    this.$message({
                        type: "info",
                        message: "写点东西吧~",
                    });
                    return;
                }
                groupApi.searchByName(this.groupName, 1).then((response) => {
                    this.tableData = response.data.data.rows;
                    this.totalSize = response.data.data.allNum;
                });
            },
            delAll() {
                this.multipleSelection = [];
            },
            handleSelectionChange(val) {
                this.multipleSelection = val;
            },
            // 删除群聊
            handleDelete(id, row) {
                this.$confirm('此操作将永久删除该群聊, 是否继续?', '警告', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'error'
                }).then(() => {
                    groupApi.deleteGroup(id).then((response) => {
                        this.$notify({
                            title: (response.data.flag ? '删除成功' : '删除失败'),
                            type: (response.data.flag ? 'success' : 'error'),
                            message: response.data.message,
                        });
                        if (response.data.flag) {
                            const index = this.tableData.indexOf(row);
                            this.tableData.splice(index, 1);
                        }
                    });
                });
            },
        }
    }

</script>

<style scoped>
    .handle-box {
        margin-bottom: 20px;
    }

    .handle-select {
        width: 120px;
    }

    .handle-input {
        width: 300px;
        display: inline-block;
    }

    .del-dialog-cnt {
        font-size: 16px;
        text-align: center
    }

    .table {
        width: 100%;
        font-size: 14px;
    }

    .red {
        color: #ff0000;
    }

    .mr10 {
        margin-right: 10px;
    }
</style>
