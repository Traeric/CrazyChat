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
	import {
		getUser
	} from "../../utils/auth";


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
				if (newPwd.trim() === "") {
					this.$message({
						message: "密码不能为空！",
						type: "error",
					});
					return;
				}
				// 修改密码
				userApi.modifyPwassord(getUser().id, newPwd).then((response) => {
					this.$notify({
						message: response.data.message,
						title: (response.data.flag ? '成功' : '失败'),
						type: (response.data.flag ? 'success' : 'error'),
					});
					if (response.data.flag) {
						this.newPwd = "";
						this.confirmPwd = "";
					}
				});
			},
			createAccount() {
				let adminName = this.account_name;
				let pwd = this.pwd;
				let confirm = this.caccountConfirmPwd;
				if (adminName.trim() === "") {
					this.$message({
						message: "用户名不能为空！",
						type: "error",
					});
					return;
				}
				if (pwd.trim() === "") {
					this.$message({
						message: "密码不能为空！",
						type: "error",
					});
					return;
				}
				if (pwd !== confirm) {
					this.$message({
						message: "两次密码输入不一致！",
						type: "error",
					});
					return;
				}
				// 发送请求创建账号
				userApi.createAccount(adminName, pwd).then((response) => {
					if (response.data.flag) {
						this.account_name = "";
						this.pwd = "";
						this.caccountConfirmPwd = "";
						// 提示
						this.$notify({
							title: '成功',
							type: 'success',
							dangerouslyUseHTMLString: true,    // 允许html
							duration: 0,     // 不会关闭
							message: `
								<p style="font-weight: bolder; color: #67C23A;">创建成功</p>
								<p><span style="font-weight: bolder;">管理员账号： </span>${adminName}</p>
								<p><span style="font-weight: bolder;">管理员密码： </span>${pwd}</p>
								<p><small><i class="el-icon-warning" style="color: #67C23A;"></i> 请及时保存账户信息，以免遗忘。</smalll></p>
							`,
						});
					} else {
						this.$message({
							message: response.data.message,
							type: "error",
						});
					}
				});
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
