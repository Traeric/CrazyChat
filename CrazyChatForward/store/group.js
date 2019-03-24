export const state = () => ({
    addGroupDialog: false,
    refreshGroupList: null,
});


export const mutations = {
    setaddGroupDialog(state, flag) {
        state.addGroupDialog = flag;
    },
    setRefreshGroupList(state, data) {
        state.refreshGroupList = data;
    },
};


export const actions = {
    setaddGroupDialog({commit}, flag) {
        commit("setaddGroupDialog", flag);
    },
    setRefreshGroupList({commit}, data) {
        commit("setRefreshGroupList", data);
    },
};




