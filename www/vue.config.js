const MonocoEditorPlugin = require('monaco-editor-webpack-plugin');
const path = require('path');
const ZipPlugin = require('zip-webpack-plugin');
module.exports = {
    lintOnSave: true,
    productionSourceMap: false,
    transpileDependencies: [],
    configureWebpack: {
        plugins: [
            new MonocoEditorPlugin({
                languages: ['java', 'javascript', 'json']
            }),
            new ZipPlugin({
                path: path.join(__dirname, 'dist'),
                filename: 'dist.zip'
            })
        ]
    }
};
