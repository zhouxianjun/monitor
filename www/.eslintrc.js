module.exports = {
  root: true,
  env: {
    browser: true,
    node: true
  },
  globals: {
    monaco: true
  },
  'extends': [
    'plugin:vue/essential',
    '@vue/standard'
  ],
  rules: {
    indent: [
      'error',
      4,
      {
        SwitchCase: 1
      }
    ],
    quotes: [
      'error',
      'single'
    ],
    semi: [
      'error',
      'always'
    ],
    'no-console': 'error',
    'no-debugger': 'off',
    'no-empty': 2,
    'no-eq-null': 2,
    'no-new': 2,
    'no-fallthrough': 2,
    'no-unreachable': 2,
    'no-var': 2,
    'no-return-assign': 0,
    'vue/attribute-hyphenation': [
      'error',
      'always'
    ],
    'vue/html-indent': [
      'error',
      4
    ],
    'vue/require-prop-types': 'error',
    'vue/order-in-components': 'error',
    'vue/no-parsing-error': [
      2,
      {
        'x-invalid-end-tag': false
      }
    ],
    'vue/html-quotes': [
      'error',
      'double'
    ]
  },
  parserOptions: {
    parser: 'babel-eslint'
  }
}
