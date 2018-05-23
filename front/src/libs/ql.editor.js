const provide = {
    log: {
        info: 'Function',
        debug: 'Function',
        warn: 'Function',
        error: 'Function',
    },
    rule: {
        id: 'Property',
        appId: 'Property',
        name: 'Property',
        metric: 'Property',
        count: 'Property',
        currentCount: 'Property',
        resource: 'Property',
        interval: 'Property',
        script: 'Property',
        silenceInterval: 'Property',
        alarmGroupId: 'Property',
        alarmCallback: 'Property',
        nodata: 'Property',
        status: 'Property',
        lastHistory: 'Property',
        createTime: 'Property'
    },
    history: {
        id: 'Property',
        ruleId: 'Property',
        duration: 'Property',
        createTime: 'Property',
        status: 'Property'
    }
};
export default () => {
    monaco.languages.registerCompletionItemProvider('java', {
        triggerCharacters: ['.'],
        provideCompletionItems: (model, position, token) => {
            let defaultItems = [{
                label: 'rule',
                kind: monaco.languages.CompletionItemKind.Variable
            }, {
                label: 'data',
                kind: monaco.languages.CompletionItemKind.Variable
            }, {
                label: 'history',
                kind: monaco.languages.CompletionItemKind.Variable
            }, {
                label: 'dataBatch',
                kind: monaco.languages.CompletionItemKind.Variable
            }, {
                label: 'log',
                kind: monaco.languages.CompletionItemKind.Variable
            }, {
                label: 'in',
                kind: monaco.languages.CompletionItemKind.Keyword
            }, {
                label: 'instanceof',
                kind: monaco.languages.CompletionItemKind.Keyword
            }, {
                label: 'import',
                kind: monaco.languages.CompletionItemKind.Keyword
            }, {
                label: 'if',
                kind: monaco.languages.CompletionItemKind.Snippet,
                insertText: {
                    value: [
                        'if (${1:condition}) {',
                        '\t$0',
                        '}'
                    ].join('\n')
                }
            }, {
                label: 'ifelse',
                kind: monaco.languages.CompletionItemKind.Snippet,
                insertText: {
                    value: [
                        'if (${1:condition}) {',
                        '\t$0',
                        '} else {',
                        '\t',
                        '}'
                    ].join('\n')
                }
            }, {
                label: 'function',
                kind: monaco.languages.CompletionItemKind.Snippet,
                insertText: {
                    value: [
                        'function ${1:f}() {',
                        '\t$0',
                        '}'
                    ].join('\n')
                }
            }];
            let text = model.getValueInRange({startLineNumber: position.lineNumber, startColumn: 1, endLineNumber: position.lineNumber, endColumn: position.column});
            if (text.endsWith('.')) {
                text = text.substring(0, text.length - 1);
                let result = Reflect.ownKeys(provide[text] || {}).map(k => Object.create({label: k, kind: monaco.languages.CompletionItemKind[provide[text][k]]}));
                if (result && result.length) {
                    return result;
                }
            }
            return defaultItems;
        }
    });
};