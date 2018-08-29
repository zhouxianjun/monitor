import { getTagNavListFromLocalstorage } from '@/libs/util';
import { mapMutations } from 'vuex';
export default {
    methods: {
        ...mapMutations([
            'setTagNavList'
        ]),
        tagNavBack (name) {
            if (name) {
                const res = getTagNavListFromLocalstorage().filter(item => item.name !== name);
                this.setTagNavList(res);
            }
            this.$router.back();
        }
    }
};
