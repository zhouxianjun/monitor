export default {
    data () {
        return {
            model: false,
            vo: {
                id: null
            },
            loadingBtn: false,
            formRef: 'form',
            addUrl: null,
            updateUrl: null,
            removeUrl: null,
            method: 'post',
            removeMethod: 'post',
            addTitle: '新增',
            updateTitle: '修改'
        };
    },
    computed: {
        isUpdate () {
            return this.vo.id !== null && this.vo.id !== undefined;
        },
        postUrl () {
            return this.isUpdate ? this.updateUrl : this.addUrl;
        },
        modelTitle () {
            return this.isUpdate ? this.updateTitle : this.addTitle;
        },
        generateAddOrUpdate () {
            return {
                method: this.method,
                data: this.method === 'post'.toLocaleLowerCase() ? this.vo : null,
                params: this.method === 'get'.toLocaleLowerCase() ? this.vo : null
            };
        }
    },
    methods: {
        generateRemove (item) {
            return {
                method: this.removeMethod,
                data: this.removeMethod === 'post'.toLocaleLowerCase() ? {id: item.id} : null,
                params: this.removeMethod === 'get'.toLocaleLowerCase() ? {id: item.id} : null
            };
        },
        async beforeAddOrUpdate () {},
        async afterAddOrUpdate () {},
        async addOrUpdate () {
            this.$refs[this.formRef].validate(async (valid) => {
                if (valid) {
                    const before = this.beforeAddOrUpdate();
                    let success = true;
                    if (before !== false) {
                        success = await this.fetch(this.postUrl, this.generateAddOrUpdate);
                    }
                    if (success === false) {
                        this.resetLoadingBtn();
                    } else {
                        this.model = false;
                        this._timeoutQuery();
                    }
                    await this.afterAddOrUpdate(success);
                } else {
                    this.resetLoadingBtn();
                    this.$Message.error('表单验证失败!');
                }
            });
        },
        async beforeRemove () {},
        async afterRemove () {},
        async remove (item) {
            if (!item) return;
            let before = await this.beforeRemove();
            let success = true;
            if (before !== false) {
                success = await this.fetch(this.removeUrl, this.generateRemove(item));
            }
            if (success === false) {
                this.resetLoadingBtn();
            } else {
                this._timeoutQuery();
            }
            await this.afterRemove(success);
        },
        add () {
            this.model = true;
            this.loadingBtn = true;
            this.$refs[this.formRef].resetFields();
            this.vo.id = null;
        },
        update (item) {
            this.model = true;
            this.loadingBtn = true;
            Object.keys(this.vo).forEach(key => this.vo[key] = item[key]);
        },
        cancel () {
            this.loadingBtn = false;
        },
        resetLoadingBtn () {
            this.loadingBtn = false;
            this.$nextTick(() => this.loadingBtn = true);
        },
        _timeoutQuery () {
            if (typeof this.doQuery === 'function') {
                setTimeout(() => this.doQuery(), 500);
            }
        }
    }
};
