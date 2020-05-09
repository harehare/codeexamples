const webpack = require("webpack");
const CopyPlugin = require("copy-webpack-plugin");

module.exports = {
    entry: {
        index: "./src/index.ts",
    },
    module: {
        rules: [
            {
                test: /\.ts$/,
                exclude: /node_modules/,
                use: "ts-loader",
            },
            {
                test: /\.css$/,
                use: ["css-loader", "style-loader"],
            },
        ],
    },
    plugins: [
        new webpack.EnvironmentPlugin([
            "FIREBASE_API_KEY",
            "FIREBASE_AUTH_DOMAIN",
            "FIREBASE_DATABASE_URL",
            "FIREBASE_PROJECT_ID",
            "FIREBASE_APP_ID",
        ]),
        new CopyPlugin([
            {
                from: "assets",
                to: `${__dirname}/../client/target/scala-2.13/scalajs-bundler/main`,
            },
        ]),
    ],
    output: {
        path: `${__dirname}/../client/target/scala-2.13/scalajs-bundler/main/dist`,
        library: "codeexamples",
        libraryTarget: "umd",
        filename: "codeexamples.js",
    },
    resolve: {
        extensions: [".ts", ".js", ".css", ".scss"],
    },
};
