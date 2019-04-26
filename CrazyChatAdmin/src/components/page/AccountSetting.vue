<template>
	<div>
		<div class="crumbs">
			<el-breadcrumb separator="/">
				<el-breadcrumb-item><i class="el-icon-setting"></i> 账号设置</el-breadcrumb-item>
			</el-breadcrumb>
		</div>
		<div class="setting_page">
			<!-- 修改密码 -->
			<el-card class="box-card">
				<div slot="header" class="clearfix">
					<span>修改密码</span>
				</div>
				<div class="text item">
					<el-form status-icon label-width="100px" class="demo-ruleForm">
						<el-form-item label="新密码" prop="pass">
							<el-input type="password" v-model="newPwd" auto-complete="off"></el-input>
						</el-form-item>
						<el-form-item label="确认密码" prop="checkPass">
							<el-input type="password" auto-complete="off" v-model="confirmPwd"></el-input>
						</el-form-item>
						<el-form-item>
							<el-button type="primary" @click="modifyPassword">修改</el-button>
							<el-button @click="resetForm()">重置</el-button>
						</el-form-item>
					</el-form>
				</div>
			</el-card>
			<!-- 创建账号 -->
			<el-card class="box-card">
				<div slot="header" class="clearfix">
					<span>创建账号</span>
				</div>
				<div class="text item">
					<el-form status-icon label-width="100px" class="demo-ruleForm">
						<el-form-item label="用户名" prop="pass">
							<el-input type="text" v-model="account_name" auto-complete="off"></el-input>
						</el-form-item>
						<el-form-item label="密码" prop="pass">
							<el-input type="password" v-model="pwd" auto-complete="off"></el-input>
						</el-form-item>
						<el-form-item label="确认密码" prop="checkPass">
							<el-input type="password" auto-complete="off" v-model="caccountConfirmPwd"></el-input>
						</el-form-item>
						<el-form-item>
							<el-button type="success" @click="createAccount">创建账号</el-button>
							<el-button @click="resetAccount()">重置</el-button>
						</el-form-item>
					</el-form>
				</div>
			</el-card>
		</div>
	</div>
</template>

<script>
	import userApi from "../../api/user";
	import {getUser} from "../../utils/auth";
	
	
	export default {
		data() {
			return {
				newPwd: "",
				confirmPwd: "",
				
				// 创建账号
				account_name: "",
				pwd: "",
				caccountConfirmPwd: "",
			};
		},
		methods: {
			resetForm() {
				this.newPwd = "";
				this.confirmPwd = "";
			},
			resetAccount() {
				this.account_name = "";
				this.pwd = "";
				this.caccountConfirmPwd = "";
			},
			modifyPassword() {
				let newPwd = this.newPwd;
				let confirmPwd = this.confirmPwd;
				if (newPwd !== confirmPwd) {
					this.$message({
						message: "两次输入密码不相符！",
						type: "info",
					});
					return;
				}
				// 修改密码
				userApi.modifyPwassord(getUser().id, newPwd).then((response) => {
					this.$message({
						message: response.data.message,
						type: (response.data.flag ? 'success' : 'error'),
					});
					if (response.data.flag) {
						this.newPwd = "";
						this.confirmPwd = "";
					}
				});
			},
			createAccount() {
				alert(2);
			},
		},
	}
</script>


<style scoped>
	.text {
		font-size: 14px;
	}

	.item {
		margin-bottom: 18px;
	}

	.clearfix:before,
	.clearfix:after {
		display: table;
		content: "";
	}

	.clearfix:after {
		clear: both
	}

	.setting_page {
		display: flex;
	}

	.box-card {
		flex: 1;
		margin: 0 20px;
	}
</style>
