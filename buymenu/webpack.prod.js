const path = require('path');
const TerserPlugin = require("terser-webpack-plugin");
const TsconfigPathsPlugin = require('tsconfig-paths-webpack-plugin');

module.exports = {
    entry: './js/Root.tsx',
    cache: true,
    mode: 'production',
    output: {
        path: path.resolve('./src/main/resources/static/'),
        filename: 'built/bundle.js',
        publicPath: '/',
        assetModuleFilename: 'built/asset/[hash][ext][query]'
    },
    optimization: {
        minimize: true,
        minimizer: [new TerserPlugin()],
    },
    module: {
        rules: [
            {
                test: /\.(js|jsx|tsx|ts)$/,
                use: {
                    loader: 'babel-loader',
                },
                exclude: /node_modules/,
            },
            {
                test: /\.(less)$/,
                use: [
                    'style-loader',
                    'css-loader',
                    'less-loader'
                ]
            },
            {
                test: /\.s[ac]ss|css$/i,
                use: [
                    "style-loader",
                    "css-loader",
                    "sass-loader",
                ],
            },
        ]
    },
    plugins: [
    ],
    resolve: {
        plugins: [new TsconfigPathsPlugin({})],
        extensions: ['.ts', '.tsx', '.js', '.less'],
    },
};