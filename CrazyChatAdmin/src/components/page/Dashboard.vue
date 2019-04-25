<template>
    <div>
        <el-row :gutter="20">
            <el-col :span="8">
                <el-card shadow="hover" class="mgb20" style="height:252px;">
                    <div class="user-info">
                        <img src="../../assets/img/img.jpg" class="user-avator" alt="">
                        <div class="user-info-cont">
                            <div class="user-info-name">{{ userName }}</div>
                            <div>管理员你好！</div>
                        </div>
                    </div>
                    <div class="user-info-list">当前登录时间：<span>{{ currentDate }}</span></div>
                    <div class="user-info-list">当前登录用户：<span>{{ userName }}</span></div>
                </el-card>
                <el-card shadow="hover" style="height:252px;">
                    <div slot="header" class="clearfix">
                        <span>网站语言详情</span>
                    </div>
                    java
                    <el-progress :percentage="50" color="#b07219"></el-progress>
                    JavaScript
                    <el-progress :percentage="24" color="#f1e05a"></el-progress>
                    CSS
                    <el-progress :percentage="8"></el-progress>
                    HTML
                    <el-progress :percentage="8" color="#f56c6c"></el-progress>
                    Vue
					<el-progress :percentage="10" color="#42b983"></el-progress>
                </el-card>
            </el-col>
            <el-col :span="16">
                <el-row :gutter="20" class="mgb20">
                    <el-col :span="8">
                        <el-card shadow="hover" :body-style="{padding: '0px'}">
                            <div class="grid-content grid-con-1">
                                <i class="el-icon-lx-people grid-con-icon"></i>
                                <div class="grid-cont-right">
                                    <div class="grid-num">{{ groupNum }}</div>
                                    <div>创建的群聊数量</div>
                                </div>
                            </div>
                        </el-card>
                    </el-col>
                    <el-col :span="8">
                        <el-card shadow="hover" :body-style="{padding: '0px'}">
                            <div class="grid-content grid-con-2">
                                <i class="el-icon-lx-news grid-con-icon"></i>
                                <div class="grid-cont-right">
                                    <div class="grid-num">{{ registerUser }}</div>
                                    <div>注册用户总数</div>
                                </div>
                            </div>
                        </el-card>
                    </el-col>
                    <el-col :span="8">
                        <el-card shadow="hover" :body-style="{padding: '0px'}">
                            <div class="grid-content grid-con-3">
                                <i class="el-icon-lx-profile grid-con-icon"></i>
                                <div class="grid-cont-right">
                                    <div class="grid-num">{{ adminNum }}</div>
                                    <div>管理员数量</div>
                                </div>
                            </div>
                        </el-card>
                    </el-col>
                </el-row>
                <el-card shadow="hover" style="height:403px;">
                    <div slot="header" class="clearfix">
                        <span>待办事项</span>
                        <el-button style="float: right; padding: 3px 0" type="text" @click="addTodo">
                            添加
                        </el-button>
                    </div>
                    <el-table :data="todoList" :show-header="false" height="304" style="width: 100%;font-size:14px;"
                              @row-click="changeStatus">
                        <el-table-column width="40">
                            <template slot-scope="scope">
                                <el-checkbox v-model="scope.row.status"></el-checkbox>
                            </template>
                        </el-table-column>
                        <el-table-column>
                            <template slot-scope="scope">
                                <div class="todo-item" :class="{'todo-item-del': scope.row.status}">
                                    {{scope.row.title}}
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column width="60">
                            <template slot-scope="scope">
                                <i class="el-icon-delete" style="cursor: pointer; color: #f00;"
                                   @click="deleteTodo(scope.row.tag)"></i>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-col>
        </el-row>
        <!--
        <el-row :gutter="20">
            <el-col :span="12">
                <el-card shadow="hover">

                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card shadow="hover">

                </el-card>
            </el-col>
        </el-row>
        -->
    </div>
</template>

<script>
    import {getUser} from "../../utils/auth";
    import userApi from "../../api/user";

    export default {
        name: 'dashboard',
        data() {
            return {
                userName: getUser().name,
                currentDate: "",
                groupNum: 0,    // 用户登录数
                registerUser: 0,  // 注册用户数
                adminNum: 0,    // 管理员数量
                todoList: [],
            }
        },
        created() {
            const date = new Date();
            this.currentDate = date.getFullYear() + "年" + date.getMonth() + "月"
                + date.getDay() + "日 " + date.getHours() + ":" + date.getMinutes();
            // 查询用户数量，管理员，群聊数量
            userApi.UserNum().then((response) => {
                if (response.data.flag) {
                    this.groupNum = response.data.data.groupNum;
                    this.registerUser = response.data.data.registerNum;
                    this.adminNum = response.data.data.adminNum;
                }
            });
        },
        mounted() {
            // 加载待办事项
            this.todoList = JSON.parse(localStorage.getItem("todoList")) || [];
        },
        methods: {
            // 添加备忘录
            addTodo() {
                this.$prompt('请填写备忘项', '备忘录', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                }).then(({value}) => {
                    if (value === null || value.trim() === "") {
                        this.$message({
                            type: 'success',
                            message: '写点东西吧',
                        });
                        return false;
                    }
                    // 添加到localStorage
                    let obj = {
                        title: value,
                        status: false,
                        tag: new Date().getTime(),
                    };
                    this.todoList.push(obj);
                    localStorage.setItem("todoList", JSON.stringify(this.todoList));
                });
            },
            changeStatus() {
                // 保存到localStorage
                localStorage.setItem("todoList", JSON.stringify(this.todoList));
            },
            deleteTodo(tag) {
                // 找出当前的删除项
                this.todoList.forEach((item) => {
                    if (item.tag === tag) {
                        // 删除
                        const index = this.todoList.indexOf(item);
                        this.todoList.splice(index, 1);
                    }
                });
                // 保存到localStorage
                localStorage.setItem("todoList", JSON.stringify(this.todoList));
            },
        }
    }

</script>


<style scoped>
    .el-row {
        margin-bottom: 20px;
    }

    .grid-content {
        display: flex;
        align-items: center;
        height: 100px;
    }

    .grid-cont-right {
        flex: 1;
        text-align: center;
        font-size: 14px;
        color: #999;
    }

    .grid-num {
        font-size: 30px;
        font-weight: bold;
    }

    .grid-con-icon {
        font-size: 50px;
        width: 100px;
        height: 100px;
        text-align: center;
        line-height: 100px;
        color: #fff;
    }

    .grid-con-1 .grid-con-icon {
        background: rgb(45, 140, 240);
    }

    .grid-con-1 .grid-num {
        color: rgb(45, 140, 240);
    }

    .grid-con-2 .grid-con-icon {
        background: rgb(100, 213, 114);
    }

    .grid-con-2 .grid-num {
        color: rgb(45, 140, 240);
    }

    .grid-con-3 .grid-con-icon {
        background: rgb(242, 94, 67);
    }

    .grid-con-3 .grid-num {
        color: rgb(242, 94, 67);
    }

    .user-info {
        display: flex;
        align-items: center;
        padding-bottom: 20px;
        border-bottom: 2px solid #ccc;
        margin-bottom: 20px;
    }

    .user-avator {
        width: 120px;
        height: 120px;
        border-radius: 12px;
    }

    .user-info-cont {
        padding-left: 50px;
        flex: 1;
        font-size: 14px;
        color: #999;
    }

    .user-info-cont div:first-child {
        font-size: 30px;
        color: #222;
    }

    .user-info-list {
        font-size: 14px;
        color: #999;
        line-height: 25px;
    }

    .user-info-list span {
        margin-left: 70px;
    }

    .mgb20 {
        margin-bottom: 20px;
    }

    .todo-item {
        font-size: 14px;
    }

    .todo-item-del {
        text-decoration: line-through;
        color: #999;
    }

    .schart {
        width: 100%;
        height: 300px;
    }

</style>
