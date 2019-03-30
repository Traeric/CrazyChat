<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item><i class="el-icon-lx-mail"></i> 发送公告</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container clear-float">
            <div class="plugins-tips">
                <div class="info">在此编辑公告，选中推送人即可推送公告</div>
                <el-radio v-model="sendType" label="1">全部发送</el-radio>
                <el-radio v-model="sendType" label="2">
                    指定推送用户
                    <el-select v-model="selectList" filterable placeholder="请选择" multiple>
                        <el-option
                                v-for="item in userList"
                                :key="item.name"
                                :label="item.name"
                                :value="item.id">
                        </el-option>
                    </el-select>
                </el-radio>
            </div>
            <mavon-editor :toolbars="configs" v-model="content" ref="md" @change="change" style="min-height: 600px"/>
            <el-button class="editor-btn" type="primary" @click="submit" style="float: right;">推送公告</el-button>
        </div>
    </div>
</template>

<script>
    import {mavonEditor} from 'mavon-editor'
    import 'mavon-editor/dist/css/index.css'
    import userApi from "../../api/user";

    export default {
        data: function () {
            return {
                content: '',    // 原内容
                html: '',       // 替换后的内容
                sendType: "1",
                selectList: [],  // 被选中用户的id
                userList: [],   // 初始化用户列表
                configs: {
                    bold: true, // 粗体
                    italic: true, // 斜体
                    header: true, // 标题
                    underline: true, // 下划线
                    strikethrough: true, // 中划线
                    mark: true, // 标记
                    superscript: true, // 上角标
                    subscript: true, // 下角标
                    quote: true, // 引用
                    ol: true, // 有序列表
                    ul: true, // 无序列表
                    link: true, // 链接
                    code: true, // code
                    table: true, // 表格
                    fullscreen: true, // 全屏编辑
                    readmodel: true, // 沉浸式阅读
                    htmlcode: true, // 展示html源码
                    help: true, // 帮助
                    /* 1.3.5 */
                    undo: true, // 上一步
                    redo: true, // 下一步
                    trash: true, // 清空
                    save: true, // 保存（触发events中的save事件）
                    /* 1.4.2 */
                    navigation: true, // 导航目录
                    /* 2.1.8 */
                    alignleft: true, // 左对齐
                    aligncenter: true, // 居中
                    alignright: true, // 右对齐
                    /* 2.2.1 */
                    subfield: true, // 单双栏模式
                    preview: true, // 预览
                },
            }
        },
        created() {
            // 加载所有的用户
            userApi.findAll().then((response) => {
                this.userList = response.data.data;
            });
        },
        components: {
            mavonEditor,
        },
        methods: {
            change(value, render) {
                // render 为 markdown 解析后的结果
                this.html = render;
            },
            // 提交按钮
            submit() {
                // 检查内容是否为空
                if (this.html.trim() === "") {
                    this.$message.error('内容不能为空');
                    return;
                }
                // 获取发送对象
                if (this.sendType === "1") {
                    // 发送给全部用户
                    userApi.sendNoticeToAll(this.html).then((response) => {
                        this.$notify({
                            title: (response.data.flag ? '推送成功' : '推送失败'),
                            type: (response.data.flag ? 'success' : 'error'),
                            message: response.data.message,
                        });
                        if (response.data.flag) {
                            // 清空内容
                            this.html = "";
                            this.content = "";
                        }
                    });
                } else {
                    // 发送给指定用户
                    // 检查发送列表
                    if (this.selectList.length === 0) {
                        this.$message.error("请选择推送用户");
                        return;
                    }
                    userApi.sendNoticeToSelect(this.html, this.selectList).then((response) => {
                        this.$notify({
                            title: (response.data.flag ? '推送成功' : '推送失败'),
                            type: (response.data.flag ? 'success' : 'error'),
                            message: response.data.message,
                        });
                        if (response.data.flag) {
                            // 清空内容
                            this.html = "";
                            this.content = "";
                        }
                    });
                }
            }
        }
    }
</script>
<style scoped>
    .editor-btn {
        margin-top: 20px;
    }

    .clear-float::after {
        content: '';
        display: block;
        clear: both;
    }
</style>