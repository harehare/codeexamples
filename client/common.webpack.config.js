const HTMLWebpackPlugin = require("html-webpack-plugin");
const PreloadWebpackPlugin = require("preload-webpack-plugin");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

module.exports = {
    entry: {
        styles: "../../../../styles.scss",
    },
    output: {
        publicPath: "/",
    },
    externals: {
        CodeDocs: "codeexamples",
    },
    resolve: {
        extensions: [".js", ".ts", ".scss", ".css"],
    },
    plugins: [
        new HTMLWebpackPlugin({
            template: "../../../../../index.html",
            inject: "body",
            inlineSource: ".css$",
            base: "/",
        }),
        new PreloadWebpackPlugin({
            rel: "preload",
            include: ["runtime", "vendors"],
        }),
        new MiniCssExtractPlugin({ filename: "[name]-[hash].css" }),
    ],
    module: {
        rules: [
            {
                test: /\.(jpe?g|png|gif|svg)$/i,
                use: [
                    {
                        loader: "file-loader",
                        options: {
                            hash: "sha512",
                            digest: "hex",
                            name: "[hash].[ext]",
                        },
                    },
                    {
                        loader: "image-webpack-loader",
                        options: {
                            bypassOnDebug: true,
                            query: {
                                mozjpeg: {
                                    progressive: true,
                                },
                                gifsicle: {
                                    interlaced: true,
                                },
                                optipng: {
                                    optimizationLevel: 7,
                                },
                            },
                        },
                    },
                ],
            },
            {
                test: /\.scss$/,
                exclude: [/node_modules/],
                loaders: [
                    "style-loader",
                    "css-loader?url=false",
                    "sass-loader",
                ],
            },
            {
                test: /\.css$/,
                loaders: ["style-loader", "css-loader?url=false"],
            },
            {
                test: /\.(png|jpg|gif|svg|eot|ttf|woff|woff2)$/,
                loader: "url-loader",
                options: {
                    limit: 10000,
                },
            },
            {
                test: /\.svg$/,
                loader: "svg-inline-loader",
            },
        ],
    },
};
