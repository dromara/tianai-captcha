const webpack = require('webpack')
const {merge} = require("webpack-merge")
const devConfig = require("./webpack.config.dev")
const prodConfig = require("./webpack.config.prod")
const path = require("path");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const {CleanWebpackPlugin} = require("clean-webpack-plugin");

const commonConfig = {
    mode: 'development',
    entry: "./src/index.js",
    output: {
        filename: "tac.js",
        path: path.resolve(__dirname, "./dist")
    },
    resolve: {
        alias: {
            "@": path.join(__dirname, "./src") // 这样@符号就表示项目根目录中src这一层路径
        }
    },
    module: {
        rules: [
            {
                test: /\.(css)$/,
                use: [MiniCssExtractPlugin.loader, 'css-loader'],
            },
            {
                test: /\.s[ac]ss$/,
                use: [MiniCssExtractPlugin.loader, "css-loader", "sass-loader"],
            },
            {
                test: /\.(png|svg|jpg|jpeg|gif)$/i,
                use: {
                    loader: 'file-loader',
                    options: {
                        esModule: false,
                        name: '[name].[ext]',
                        outputPath: 'tac/images'
                    }
                },
                type: 'javascript/auto'
            },
            // {
            //     test: /\.js$/,
            //     exclude: /node_modules/,
            //     loader: 'babel-loader',
            //     options: {
            //         //  预设babel做怎样的兼容性处理
            //         presets: ['@babel/preset-env']
            //     }
            // }
        ]
    },
    plugins: [
        new MiniCssExtractPlugin({
            // 指定抽离的之后形成的文件名
            filename: 'tac/css/tac.css'
        }),
        new webpack.HotModuleReplacementPlugin(),
        new CleanWebpackPlugin()
    ],
    devServer: {
        // 开发时可直接访问到 ./public 下的静态资源，这些资源在开发中不必打包
        port: 3000,
        static: "./dist"
    }
}

module.exports = (env, argv) => {
    if (argv && argv.mode === 'production') {
        console.log("=============production==================")
        return merge(commonConfig, prodConfig);
    }else {
        console.log("=============development==================")
        return merge(commonConfig, devConfig);
    }
}

