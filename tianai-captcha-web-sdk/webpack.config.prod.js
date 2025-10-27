const HtmlWebpackPlugin = require("html-webpack-plugin");
const path = require("path");
const TerserPlugin = require('terser-webpack-plugin');
module.exports = {
    optimization: {
        minimize: true,
        minimizer: [new TerserPlugin({
            terserOptions: {
                compress: {
                    drop_console: true, // 移除所有的`console`语句
                },
                output: {
                    comments: false, // 去掉注释
                },
            },
            extractComments: false, // 是否将注释提取到单独的文件中
        })],
    },
    externals: {
    },
    output: {
        filename: "tac/js/tac.min.js",
        path: path.resolve(__dirname, "./dist")
    },
    plugins: [
        new HtmlWebpackPlugin({
            filename: 'index.html',
            template: './public/index-prod.html'
        })
    ]

}
