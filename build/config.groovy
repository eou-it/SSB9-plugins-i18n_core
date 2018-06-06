
withConfig(configuration) {
    inline(phase: 'CONVERSION') { source, context, classNode ->
        classNode.putNodeMetaData('projectVersion', '0.1')
        classNode.putNodeMetaData('projectName', 'i18n_core.git')
        classNode.putNodeMetaData('isPlugin', 'true')
    }
}
