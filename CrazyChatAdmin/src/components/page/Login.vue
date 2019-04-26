<template>
    <div class="login-wrap">
        <div class="ms-login">
            <div class="ms-title">CrazyChat 后台管理系统</div>
            <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="0px" class="ms-content">
                <el-form-item prop="username">
                    <el-input v-model="ruleForm.username" placeholder="username">
                        <el-button slot="prepend" icon="el-icon-lx-people"></el-button>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input type="password" placeholder="password" v-model="ruleForm.password" @keyup.enter.native="submitForm('ruleForm')">
                        <el-button slot="prepend" icon="el-icon-lx-lock"></el-button>
                    </el-input>
                </el-form-item>
                <div class="login-btn">
                    <el-button type="primary" @click="submitForm('ruleForm')">登录</el-button>
                </div>
                <p class="login-tips">Tips : 请输入后台管理账号密码。</p>
            </el-form>
        </div>
    </div>
</template>

<script>
    import userApi from "../../api/user";
    import {setUser} from "../../utils/auth";

    export default {
        data: function(){
            return {
                ruleForm: {
                    username: '',
                    password: ''
                },
                rules: {
                    username: [
                        { required: true, message: '请输入用户名', trigger: 'blur' }
                    ],
                    password: [
                        { required: true, message: '请输入密码', trigger: 'blur' }
                    ]
                }
            }
        },
        methods: {
            // 登录表单
            submitForm(formName) {
				let flag = true;
                this.$refs[formName].validate((valid) => {
                    if (!valid) {
						flag = false;
                        return false;
                    }
                });
				if (flag == false) {
					// 登陆有误
					return null;
				}

                userApi.login(this.ruleForm.username, this.ruleForm.password).then((response) => {
                    if (response.data.flag) {
                        const data = response.data.data;
                        // 登录成功，设置cookie
                        setUser(data.id, data.username, data.avatar, data.token);
						// 设置登陆时间
						const date = new Date();
						let loginTime = date.getFullYear() + "年" + date.getMonth() + "月"
						    + date.getDay() + "日 " + date.getHours() + ":" + date.getMinutes();
						// 保存到localStorage
						localStorage.setItem("currentTime", loginTime);
                        // 跳转到主页
                        this.$router.push('/');
                    } else {
                        this.$notify.error({
                            title: '失败',
                            message: response.data.message,
                        });
                    }
                });
            }
        }
    }
</script>

<style scoped>
    .login-wrap{
        position: relative;
        width:100%;
        height:100%;
        background-image: url(../../assets/img/login-bg.jpg);
        background-size: 100%;
    }
    .ms-title{
        width:100%;
        line-height: 50px;
        text-align: center;
        font-size:20px;
        color: #fff;
        border-bottom: 1px solid #ddd;
    }
    .ms-login{
        position: absolute;
        left:50%;
        top:50%;
        width:350px;
        margin:-190px 0 0 -175px;
        border-radius: 5px;
        background: rgba(255,255,255, 0.3);
        overflow: hidden;
    }
    .ms-content{
        padding: 30px 30px;
    }
    .login-btn{
        text-align: center;
    }
    .login-btn button{
        width:100%;
        height:36px;
        margin-bottom: 10px;
    }
    .login-tips{
        font-size:12px;
        line-height:30px;
        color:#fff;
    }
</style>