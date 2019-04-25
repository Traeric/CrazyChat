<template>
    <div class="table">
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item><i class="el-icon-lx-profile"></i> CrazyChat用户列表</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <!-- 操作区域 -->
            <div class="handle-box">
                <el-button type="danger" icon="delete" class="handle-del mr10" @click="delAll">批量删除</el-button>
                <el-button type="warning" icon="delete" class="handle-del mr10" @click="delFrozen">批量冻结</el-button>
                <el-select v-model="account_status" placeholder="筛选账号" class="handle-select mr10"
                           @change="filterAccount">
                    <el-option key="-1" label="所有账号" value="-1"></el-option>
                    <el-option key="0" label="正常账号" value="0"></el-option>
                    <el-option key="1" label="被冻结账号" value="1"></el-option>
                </el-select>
                <el-input v-model="username" placeholder="通过用户名筛选" class="handle-input mr10"></el-input>
                <el-button type="primary" icon="search" @click="search">搜索</el-button>
            </div>
            <!-- 数据展示区域 -->
            <el-table :data="tableData" border class="table" ref="multipleTable"
                      @selection-change="handleSelectionChange" :highlight-current-row="true">
                <el-table-column type="selection" width="50" align="center"></el-table-column>
                <el-table-column type="expand" label="pull">
                    <template slot-scope="props">
                        <el-form label-position="left" class="table-expand">
                            <el-form-item label="用户id: " style="font-weight: bolder;">
                                <span style="font-weight: normal;">{{ props.row.id }}</span>
                            </el-form-item>
                            <el-form-item label="用户名称: " style="font-weight: bolder;">
                                <span style="font-weight: normal;">{{ props.row.name }}</span>
                            </el-form-item>
                            <el-form-item label="邮箱: " style="font-weight: bolder;">
                                <span style="font-weight: normal;">{{ props.row.email }}</span>
                            </el-form-item>
                            <el-form-item label="签名: " style="font-weight: bolder;">
                                <span style="font-weight: normal;">{{ props.row.sign }}</span>
                            </el-form-item>
                            <el-form-item label="生日: " style="font-weight: bolder;">
                                <span style="font-weight: normal;">{{ props.row.birthday }}</span>
                            </el-form-item>
                            <el-form-item label="性别: " style="font-weight: bolder;">
                                <span style="font-weight: normal;">{{ props.row.gender === '0' ? '男' : '女' }}</span>
                            </el-form-item>
                            <el-form-item label="个人简介: " style="font-weight: bolder;">
                                <span style="font-weight: normal;">{{ props.row.userdescribe }}</span>
                            </el-form-item>
                        </el-form>
                    </template>
                </el-table-column>
                <el-table-column prop="id" label="用户id" sortable align="center"></el-table-column>
                <el-table-column label="姓名" align="center">
                    <template slot-scope="scope">
                        <el-popover trigger="hover" placement="top">
                            <p>姓名: {{ scope.row.name }}</p>
                            <p>
                                <img style="width: 60px; height: 60px; border-radius: 12px;" :src="scope.row.avatar"
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
                <el-table-column prop="email" label="邮箱" align="center"></el-table-column>
                <el-table-column label="性别" align="center">
                    <template slot-scope="scope">
                        <div slot="reference" class="name-wrapper" style="cursor: pointer;">
                            <el-tag size="medium" :type="'warning'">
                                {{ scope.row.gender === '0' ? '男 male' : '女 female' }}
                            </el-tag>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="账号状态" align="center">
                    <template slot-scope="scope">
                        <div slot="reference" class="name-wrapper" style="cursor: pointer;">
                            <el-tag size="medium" :type="'success'" v-if="scope.row.status === 0">
                                正常
                            </el-tag>
                            <el-tag size="medium" :type="'danger'" v-if="scope.row.status === 1">
                                已冻结
                            </el-tag>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="180" align="center">
                    <template slot-scope="scope">
                        <!-- 冻结账号 -->
                        <el-button type="text" icon="el-icon-lx-warn"
                                   @click="frozenAccount(scope.row.id, scope.row)" v-if="scope.row.status === 0">
                            冻结账号
                        </el-button>
                        <!-- 解冻 -->
                        <el-button type="text" icon="el-icon-lx-attention" style="color: #8e9092;"
                                   @click="unFrozenAccount(scope.row.id, scope.row)" v-else>
                            解冻账号
                        </el-button>

                        <el-button type="text" icon="el-icon-delete" class="red"
                                   @click="deleteAccount(scope.row.id, scope.row)">
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
    import userApi from "../../api/user";

    export default {
        data() {
            return {
                tableData: [],
                totalSize: 0,
                cur_page: 1,
                multipleSelection: [],   // 选中的列，里面是一个个对象，每个对象里面是该列的所有值
                account_status: '-1',   // select框筛选项
                username: '',   // 搜索关键字
                del_list: [],
            }
        },
        created() {
            userApi.findAllUser(1).then((response) => {
                this.tableData = response.data.data.rows;
                this.totalSize = response.data.data.allNum;
            });
        },
        methods: {
            // 点击翻页
            handleCurrentChange(val) {
                // 获取当前页数
                this.cur_page = val;
                // 查看搜索条件
                if (this.username && this.account_status !== "-1") {
                    userApi.searchByNameAndStatus(this.username, this.account_status, val).then((response) => {
                        this.tableData = response.data.data.rows;
                        this.totalSize = response.data.data.allNum;
                    });
                } else if (this.username && this.account_status === "-1") {
                    userApi.searchByName(this.username, val).then((response) => {
                        this.tableData = response.data.data.rows;
                        this.totalSize = response.data.data.allNum;
                    });
                } else if (!this.username && this.account_status !== "-1") {
                    userApi.searchUserByStatus(this.account_status, val).then((response) => {
                        this.tableData = response.data.data.rows;
                        this.totalSize = response.data.data.allNum;
                    });
                } else {
                    userApi.findAllUser(val).then((response) => {
                        this.tableData = response.data.data.rows;
                        this.totalSize = response.data.data.allNum;
                    });
                }
            },
            // 搜索用户名
            search() {
                if (!this.username) {
                    this.$message({
                        type: "info",
                        message: "写点东西吧~",
                    });
                    return;
                }
                if (this.account_status === "-1") {
                    userApi.searchByName(this.username, 1).then((response) => {
                        this.tableData = response.data.data.rows;
                        this.totalSize = response.data.data.allNum;
                    });
                } else {
                    userApi.searchByNameAndStatus(this.username, this.account_status, 1).then((response) => {
                        this.tableData = response.data.data.rows;
                        this.totalSize = response.data.data.allNum;
                    });
                }
            },
            // 筛选账号
            filterAccount() {
                if (this.account_status * 1 === -1) {
                    if (this.username) {
                        userApi.searchByName(this.username, 1).then((response) => {
                            this.tableData = response.data.data.rows;
                            this.totalSize = response.data.data.allNum;
                        });
                    } else {
                        // 获取全部用户
                        userApi.findAllUser(1).then((response) => {
                            this.tableData = response.data.data.rows;
                            this.totalSize = response.data.data.allNum;
                        });
                    }
                } else {
                    // 获取筛选用户
                    if (this.username) {
                        userApi.searchByNameAndStatus(this.username, this.account_status, 1).then((response) => {
                            this.tableData = response.data.data.rows;
                            this.totalSize = response.data.data.allNum;
                        });
                    } else {
                        userApi.searchUserByStatus(this.account_status, 1).then((response) => {
                            this.tableData = response.data.data.rows;
                            this.totalSize = response.data.data.allNum;
                        });
                    }
                }
            },
            delAll() {
                // 执行删除

                this.multipleSelection = [];
            },
            delFrozen() {
                // 批量冻结

                this.multipleSelection = [];
            },
            // 冻结账号
            frozenAccount(id, row) {
                this.$confirm('此操作将冻结该账号, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'info'
                }).then(() => {
                    userApi.changeStatus(id, 1).then((response) => {
                        this.$notify({
                            title: (response.data.flag ? '冻结成功' : '冻结失败'),
                            type: (response.data.flag ? 'success' : 'error'),
                            message: response.data.message,
                        });
                        if (response.data.flag) {
                            row.status = 1;
                        }
                    });
                });
            },
            // 解冻账号
            unFrozenAccount(id, row) {
                this.$confirm('是否要解封该账号?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'success'
                }).then(() => {
                    userApi.changeStatus(id, 0).then((response) => {
                        this.$notify({
                            title: (response.data.flag ? '解冻成功' : '解冻失败'),
                            type: (response.data.flag ? 'success' : 'error'),
                            message: response.data.message,
                        });
                        if (response.data.flag) {
                            row.status = 0;
                        }
                    });
                });
            },
            // 删除账号
            deleteAccount(id, row) {
                this.$confirm('该账号将永久删除，是否继续？', '警告', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'error'
                }).then(() => {
                    userApi.deleteUser(id).then((response) => {
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
            // 选中操作
            handleSelectionChange(val) {
                this.multipleSelection = val;
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
