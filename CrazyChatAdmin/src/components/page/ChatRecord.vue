<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item><i class="el-icon-lx-message"></i> 聊天记录</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <!-- 数据展示区域 -->
            <div class="form-box">
                <el-form ref="form" label-width="160px" :label-position="'left'">
                    <el-form-item label="选择要查看的聊天记录">
                        <el-select v-model="chatType" placeholder="请选择">
                            <el-option key="0" label="用户对用户" value="0"></el-option>
                            <el-option key="1" label="用户对群" value="1"></el-option>
                        </el-select>
                    </el-form-item>
                    <!-- 用户对用户 -->
                    <div v-if="chatType === '0'">
                        <el-form-item label="选择用户">
                            <el-select v-model="fromUser" placeholder="选择用户1">
                                <el-option v-for="(item, index) in userList" :key="index"
                                           :label="item.name"
                                           :value="item.id">
                                </el-option>
                            </el-select>
                            <span style="display: inline-block; width: 20px;"></span>
                            <el-select v-model="toUser" placeholder="选择用户2">
                                <el-option v-for="(item, index) in userList" :key="index"
                                           :label="item.name"
                                           :value="item.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </div>
                    <!-- 用户对群 -->
                    <div v-else>
                        <el-form-item label="选择用户跟群">
                            <el-select v-model="user" placeholder="选择用户" @change="getGroupList">
                                <el-option v-for="(item, index) in userList" :key="index"
                                           :label="item.name"
                                           :value="item.id">
                                </el-option>
                            </el-select>
                            <span style="display: inline-block; width: 20px;"></span>
                            <el-select v-model="group" placeholder="选择群">
                                <el-option v-for="(item, index) in groupList" :key="index"
                                           :label="item.name"
                                           :value="item.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </div>

                    <el-form-item label="选择日期">
                        <el-date-picker v-model="date" type="daterange" align="right" unlink-panels range-separator="至"
                                        start-placeholder="开始日期" end-placeholder="结束日期" :picker-options="pickerOptions">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item>
                        <el-button style="margin-left: -160px;" type="primary" @click="onSubmit">查 询 聊 天 记 录</el-button>
                    </el-form-item>
                </el-form>
            </div>
            <!-- 数据展示区域 -->
            <hr>
            <el-table :data="chatRecord" stripe style="width: 100%; margin-top: 20px;">
                <el-table-column prop="name" label="用户" width="180" align="center">
                </el-table-column>
                <el-table-column prop="content" label="内容" align="center">
                </el-table-column>
                <el-table-column prop="date" label="发送时间" width="180" align="center">
                </el-table-column>
            </el-table>
        </div>

    </div>
</template>

<script>
    import userApi from "../../api/user";
    import groupApi from "../../api/group";

    export default {
        name: 'baseform',
        data: function () {
            return {
                chatType: '0',
                userList: [],
                groupList: [],
                // 用户对用户发送
                fromUser: "",
                toUser: '',
                // 用户对群
                user: "",
                group: "",

                chatRecord: [
                    {
                        "name": "张三",
                        "content": "今天写作业",
                        "date": "2019-01-23",
                    },
                    {
                        "name": "张三",
                        "content": "今天写作业",
                        "date": "2019-01-23",
                    },
                    {
                        "name": "张三",
                        "content": "今天写作业",
                        "date": "2019-01-23",
                    },
                ],

                date: "",
                pickerOptions: {
                    shortcuts: [{
                        text: '最近一周',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                            picker.$emit('pick', [start, end]);
                        }
                    }, {
                        text: '最近一个月',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                            picker.$emit('pick', [start, end]);
                        }
                    }, {
                        text: '最近三个月',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                            picker.$emit('pick', [start, end]);
                        }
                    }]
                },
            }
        },
        created() {
            // 查询所有的用户
            userApi.findAll().then((response) => {
                this.userList = response.data.data;
            });
        },
        methods: {
            // 根据用户获取群聊
            getGroupList() {
                groupApi.getGroupByUser(this.user).then((response) => {
                    this.groupList = response.data.data;
                });
            },
            onSubmit() {
                this.$message.success('提交成功！');
            }
        },
    }
</script>