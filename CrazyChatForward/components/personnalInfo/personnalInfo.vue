<template>
    <el-dialog title="个人信息"
               :visible.sync="$store.state.user.personnalInfoDialog"
               :before-close="handleClose" width="30%" top="10vh">
        <el-form label-width="80px">
            <el-form-item label="个性昵称">
                <el-input autocomplete="off" v-model="$store.state.user.userInfo.nick"></el-input>
            </el-form-item>
            <el-form-item label="签名">
                <el-input autocomplete="off" v-model="$store.state.user.userInfo.sign"></el-input>
            </el-form-item>
            <el-form-item prop="email" label="邮箱">
                <el-input v-model="$store.state.user.userInfo.email"></el-input>
            </el-form-item>
            <el-form-item label="性别">
                <el-radio-group size="medium" v-model="$store.state.user.userInfo.gender">
                    <el-radio border label="0"
                              :disabled="$store.state.user.userInfo.gender === '1'">
                        玉皇大帝
                    </el-radio>
                    <el-radio border label="1"
                              :disabled="$store.state.user.userInfo.gender === '0'">
                        王母娘娘
                    </el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="生日">
                <el-date-picker type="date" v-model="$store.state.user.userInfo.birthday" disabled="true"></el-date-picker>
            </el-form-item>
            <el-form-item label="年龄">
                <el-input autocomplete="off" v-model="$store.state.user.userInfo.age"></el-input>
            </el-form-item>
            <el-form-item label="个人简介" prop="desc">
                <el-input type="textarea" v-model="$store.state.user.userInfo.describe"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="handleClose">关闭</el-button>
        </div>
    </el-dialog>
</template>

<script>
    export default {
        props: {
            "personnalInfoDialog": {
                type: Boolean,
                default: false,
            },
        },
        methods: {
            handleClose() {
                this.$store.dispatch("user/setpersonnalInfoDialog", false);
            }
        },
    }
</script>

<style scoped lang="stylus">

</style>
