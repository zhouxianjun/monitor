const webpack = require('webpack');
const merge = require('webpack-merge');
const webpackBaseConfig = require('./webpack.base.config.js');

module.exports = merge(webpackBaseConfig, {
    devtool: '#source-map',
    plugins: [
        new webpack.DefinePlugin({
            baseURL: '"http://127.0.0.1:8080"'
        })
    ]
});