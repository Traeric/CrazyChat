export const state = () => ({
    personnalInfoDialog: false,
    userInfo: {},
    userGroupDialog: false,
    userGroup: [],
    refreshFriendList: null,
    warnCount: 0,
});


export const mutations = {
    setpersonnalInfoDialog(state, flag) {
        state.personnalInfoDialog = flag;
    },
    setUserInfo(state, info) {
        state.userInfo = info;
    },
    setUserGroupDialog(state, flag) {
        state.userGroupDialog = flag;
    },
    setUserGroup(state, group) {
        state.userGroup = group;
    },
    setRefreshFriendList(state, data) {
        state.refreshFriendList = data;
    },
    incremWarnCount(state) {
        state.warnCount++;
    },
    decremWarnCount(state) {
        state.warnCount--;
    },
    setWarnCount(state, count) {
        state.warnCount = count;
    },
};


export const actions = {
    setpersonnalInfoDialog({commit}, flag) {
        commit("setpersonnalInfoDialog", flag);
    },
    setUserInfo({commit}, info) {
        commit("setUserInfo", info);
    },
    setUserGroupDialog({commit}, flag) {
        commit("setUserGroupDialog", flag);
    },
    setUserGroup({commit}, group) {
        commit("setUserGroup", group);
    },
    setRefreshFriendList({commit}, data) {
        commit("setRefreshFriendList", data);
    },
    incremWarnCount({commit}) {
        commit("incremWarnCount");
    },
    decremWarnCount({commit}) {
        commit("decremWarnCount");
    },
    setWarnCount({commit}, count) {
        commit("setWarnCount", count);
    },
};

