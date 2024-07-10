var path = require('path');
const TerserPlugin = require("terser-webpack-plugin");
const LodashModuleReplacementPlugin = require('lodash-webpack-plugin');
const TsconfigPathsPlugin = require('tsconfig-paths-webpack-plugin');
const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;

module.exports = {
    entry: './js/Root.tsx',
    devtool: 'source-map',
    cache: true,
    mode: 'development',
    output: {
        path: path.resolve('./src/main/resources/static/'),
        filename: 'built/bundle.js',
        publicPath: '/',
        assetModuleFilename: 'built/asset/[hash][ext][query]'
    },
    module: {
        rules: [
            {
                test: /\.(js|jsx|tsx|ts)$/,
                use: {
                    loader: 'babel-loader',
                    options: {
                    }
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
        //new BundleAnalyzerPlugin()
    ],
    resolve: {
        plugins: [new TsconfigPathsPlugin({})],
        extensions: ['.ts', '.tsx', '.js', '.less'],
    },
};