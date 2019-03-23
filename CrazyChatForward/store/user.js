export const state = () => ({
    personnalInfoDialog: false,
    userInfo: {},
    userGroupDialog: false,
    userGroup: [],
    refreshFriendList: null,
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
};

