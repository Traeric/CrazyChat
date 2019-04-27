<template>
	<div class="wrapper">
		<div class="top">
			<div class="logo"></div>
			系统公告
			<div class="links">
				<el-button type="primary" class="change-skin" plain>
					换肤
					<div class="position">
						<div class="cloth-pannel">
							<div class="item" v-for="(item, index) in skinList" :key="index">
								<div class="show" :style="'background-color:' + item.color" @click="changeSkin(item)"></div>
								<div class="text">{{ item.text }}</div>
							</div>
							<div style="clear: both;"></div>
						</div>
					</div>
				</el-button>
				<nuxt-link to="/home_page">
					<el-button type="success" plain>个人主页</el-button>
				</nuxt-link>
				<nuxt-link to="/chat_room">
					<el-button type="warning" plain>聊天室</el-button>
				</nuxt-link>
				<nuxt-link to="/chat_room">
					<el-button type="danger" plain>动态空间</el-button>
				</nuxt-link>
			</div>
		</div>

		<div class="left" ref="leftHook">
			<el-card class="box-card" v-for="(item, index) in noticeList" :key="index">
				<div class="delete" title="删除" @click="deleteNotice(index)"><i class="el-icon-delete"></i></div>
				<div class="text" @click="checkNoticet(index)">
					<a href="javascript:void(0);">{{ item.title }}</a>
				</div>
				<div class="is-read" v-if="item.read === '0'"></div>
			</el-card>
		</div>

		<div class="right">
			<el-card class="box-card notice-content" :body-style="{ padding: '0px' }">
				<div slot="header" class="clearfix">
					<span ref="titleHook"></span>
					<el-button v-if="noRead" style="float: right; padding: 3px 0" type="text" v-show="currentNotice !== -1" @click="read()">标为已读</el-button>
					<el-button v-else style="float: right; padding: 3px 0" type="text" v-show="currentNotice !== -1" @click="notRead()">标为未读</el-button>
				</div>
				<div class="content" ref="noticeHook">
				</div>
			</el-card>
		</div>

		<!-- 消息提醒 -->
		<audio id="msg_hook">
			<source src="radio/notice.wav" />
			<source src="radio/notice.mp3" />
			<source src="radio/notice.ogg" />
		</audio>
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
				noticeList: [],
				currentNotice: -1,
				wsMsg: null,
				noRead: true,
				// 皮肤颜色
				skinList: [{
						"text": "默认",
						"color": "#fff",
					},
					{
						"text": "淡蓝",
						"color": "#d9e8fb",
					},
					{
						"text": "淡紫",
						"color": "#dadcf2",
					},
					{
						"text": "淡绿",
						"color": "#e1efe7",
					},
					{
						"text": "淡粉",
						"color": "#f2e7ee",
					},
					{
						"text": "落樱",
						"color": "#ff8095",
					},
					{
						"text": "墨竹",
						"color": "#a0aeab",
					},
				],
			};
		},
		mounted() {
			// 加载所有的通知
			userApi.loadNotice(getUser().id).then((response) => {
				this.noticeList = response.data.data;
			});

			// 设置左侧栏的长度
			let height = window.innerHeight - 60;
			this.$refs.leftHook.style.height = `${height}px`;
			this.$refs.noticeHook.style.height = `${height - 130}px`;

			// 建立socket连接
			this.wsMsg = new WebSocket("ws://127.0.0.1:9002/verify_user/" + getUser().id);
			this.wsMsg.onmessage = (event) => {
				// 消息提示音
				document.getElementById("msg_hook").play();
				// 接收到消息，刷新消息列表
				userApi.loadNotice(getUser().id).then((response) => {
					this.noticeList = response.data.data;
				});
			};

		},
		methods: {
			checkNoticet(index) {
				// 获取标题
				let item = this.noticeList[index];
				// 设置到标题中
				this.$refs.titleHook.innerHTML = item.title;
				// 设置内容
				this.$refs.noticeHook.innerHTML = item.content;
				this.currentNotice = index; // 设置当前查看的公告
				this.noRead = item.read === '0' ? true : false;
			},
			// 标为已读
			read() {
				userApi.readNotice(this.noticeList[this.currentNotice].key).then((response) => {
					this.$message({
						message: response.data.message,
						type: (response.data.flag ? 'success' : 'error'),
					});

					if (response.data.flag) {
						this.noticeList[this.currentNotice].read = '1';
						this.noRead = false;
					}
				});
			},
			// 标为未读
			notRead() {
				userApi.noReadNotice(this.noticeList[this.currentNotice].key).then((response) => {
					this.$message({
						message: response.data.message,
						type: (response.data.flag ? 'success' : 'error'),
					});

					if (response.data.flag) {
						this.noticeList[this.currentNotice].read = '0';
						this.noRead = true;
					}
				});
			},
			// 删除通知
			deleteNotice(index) {
				this.$confirm('此操作将永久删除该条公告, 是否继续?', '警告', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'error'
				}).then(() => {
					userApi.deleteNotice(this.noticeList[index].key).then((response) => {
						this.$message({
							message: response.data.message,
							type: (response.data.flag ? 'success' : 'error'),
						});

						if (response.data.flag) {
							// 移除该条通告
							this.noticeList.splice(index, 1);
							// 检查该条通告是否已经展示
							if (this.currentNotice === index) {
								this.$refs.titleHook.innerHTML = "";
								this.$refs.noticeHook.innerHTML = "";
								this.currentNotice = -1;
							}
						}
					});
				});
			},
			// 换肤
			changeSkin(skin) {
				this.$refs.noticeHook.style.backgroundColor = skin.color;
			}
		},
	};
</script>

<style scoped>
	.wrapper .top {
		position: relative;
		height: 60px;
		color: #606266;
		text-align: center;
		line-height: 60px;
		font-weight: bolder;
		font-size: 22px;
		user-select: none;
		background-color: #EBEEF5;
	}

	.wrapper .top .links {
		position: absolute;
		top: 0;
		right: 10px;
		height: 60px;
	}

	.wrapper .top .links .change-skin {
		position: relative;
	}

	.wrapper .top .links .change-skin:hover .position {
		display: block;
	}
	
	.wrapper .top .links .change-skin .position {
		position: absolute;
		top: 38px;
		left: -80px;
		padding-top: 12px;
		display: none;
	}

	.wrapper .top .links .change-skin .cloth-pannel {
		padding: 10px;
		width: 230px;
		border-radius: 4px;
		box-shadow: 2px 2px 6px #A4A4A4;
		background-color: #e7f0ff;
	}

	.wrapper .top .links .change-skin .cloth-pannel .item {
		float: left;
		margin: 10px;
		width: 50px;
		height: 70px;
	}

	.wrapper .top .links .change-skin .cloth-pannel .item .show {
		width: 50px;
		height: 50px;
		border-radius: 50%;
		border: 3px solid transparent;
	}

	.wrapper .top .links .change-skin .cloth-pannel .item .show:hover {
		border-color: #606266;
	}

	.wrapper .top .links .change-skin .cloth-pannel .item .text {
		width: 50px;
		height: 20px;
		line-height: 20px;
		text-align: center;
		color: #47494E;
	}

	.wrapper .top .logo {
		position: absolute;
		z-index: 1000;
		top: 0;
		left: 50px;
		height: 80px;
		width: 150px;
		background-size: 150px 80px;
		background-image: url(../../assets/images/cloud.png);
	}

	.wrapper .left {
		float: left;
		margin-right: 30px;
		padding: 20px 10px;
		width: 15%;
		overflow: auto;
		border-right: 1px solid #A4A4A4;
		background-color: #fcfdff;
	}

	.wrapper .left .box-card:hover {
		background-color: #F2F6FC;
	}

	.wrapper .left .box-card {
		position: relative;
		user-select: none;
		cursor: pointer;
		margin-bottom: 10px;
	}

	.wrapper .left .box-card a {
		color: #606266;
	}

	.wrapper .left .box-card .is-read {
		position: absolute;
		top: 25px;
		right: 25px;
		width: 10px;
		height: 10px;
		background-color: #f00;
		border-radius: 50%;
	}

	.wrapper .left .box-card .delete i {
		color: #f77f53;
	}

	.wrapper .left .box-card .delete i:hover {
		color: #f00;
	}

	.wrapper .left .box-card .delete {
		position: absolute;
		top: 20px;
		right: 45px;
		cursor: pointer;
	}

	.wrapper .right {
		margin-top: 10px;
		float: left;
		width: 80%;
	}

	.wrapper .right .content {
		overflow: hidden;
		padding: 20px;
		background-color: #a0aeab;
	}
</style>
