export const state = () => ({
    addGroupDialog: false,
});


export const mutations = {
    setaddGroupDialog(state, flag) {
        state.addGroupDialog = flag;
    },
};


export const actions = {
    setaddGroupDialog({commit}, flag) {
        commit("setaddGroupDialog", flag);
    },
};




