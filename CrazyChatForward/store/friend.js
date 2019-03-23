export const state = () => ({
    currentNick: "",
    currentAvatar: "",
    currentId: "",
    chatRecord: [],
    groupChatRecord: [],
    addFriendDialog: false,
});


export const mutations = {
    // 设置昵称
    setCurrentNick(state, nick) {
        state.currentNick = nick;
    },
    // 设置头像
    setCurrentAvatar(state, avatar) {
        state.currentAvatar = avatar;
    },
    // 设置聊天消息
    setChatRecord(state, chatRecord) {
        state.chatRecord = chatRecord;
    },
    // 设置friend id
    setCurrentId(state, id) {
        state.currentId = id;
    },
    // 设置加好友弹窗
    setaddFriendDialog(state, flag) {
        state.addFriendDialog = flag;
    },
    // 设置群聊聊天记录
    setGroupChatRecord(state, record) {
        state.groupChatRecord = record;
    },
};


export const actions = {
    setCurrentNick({commit}, nick) {
        commit('setCurrentNick', nick);
    },
    setCurrentAvatar({commit}, avatar) {
        commit('setCurrentAvatar', avatar)
    },
    setChatRecord({commit}, chatRecord) {
        commit("setChatRecord", chatRecord);
    },
    setCurrentId({commit}, id) {
        commit("setCurrentId", id);
    },
    setaddFriendDialog({commit}, flag) {
         commit("setaddFriendDialog", flag);
    },
    setGroupChatRecord({commit}, record) {
        commit("setGroupChatRecord", record);
    },
};

