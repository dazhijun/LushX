module.exports = {
  /*
  ** Headers of the page
  */
  head: {
    title: 'lushx',
    meta: [
      { charset: 'utf-8' },
      { name: 'viewport', content: 'width=device-width, initial-scale=1' },
      { hid: 'description', name: 'description', content: 'A website for providing conveniences and happiness' }
    ],
    link: [
      { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' },
      // { rel: 'stylesheet', href: '//cdn.bootcss.com/font-awesome/4.7.0/css/font-awesome.min.css' }
    ]
  },
  /*
  ** Customize the progress bar color
  */
  loading: { color: '#00FF99' },
  /**
   * Import plugins
   */
  plugins: [
    '~plugins/iview.js',
    '~plugins/lazyload.js'
  ],
  /*
  ** Build configuration
  */
  build: {
    /**
     * Build one time
     */
    vendor: [
      'axios',
      'lodash'
    ]
    /*
    ** Run ESLint on save
    */
    // extend (config, ctx) {
    //   if (ctx.dev && ctx.isClient) {
    //     config.module.rules.push({
    //       enforce: 'pre',
    //       test: /\.(js|vue)$/,
    //       loader: 'eslint-loader',
    //       exclude: /(node_modules)/
    //     })
    //   }
    // }
  }
}
