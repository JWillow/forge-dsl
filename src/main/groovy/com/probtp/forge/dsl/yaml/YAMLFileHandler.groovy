package com.probtp.forge.dsl.yaml

import com.probtp.forge.dsl.utils.ConvertUtils
import com.probtp.forge.dsl.FileHandler
import com.probtp.forge.dsl.xml.XMLOperation
import org.xml.sax.helpers.DefaultHandler
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

class YAMLFileHandler extends DefaultHandler implements FileHandler{

    private File file
    Node node

    File getFile() {
        return file
    }

    void saveTo(Writer writer) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
        options.setPrettyFlow(true)
        Yaml parser = new Yaml(options)
        parser.dump(ConvertUtils.convertFromNode(node), writer)
    }

    void save() {
        PrintWriter printWriter = new PrintWriter(file)
        saveTo(printWriter)
    }

    Object propertyMissing(String path) {
        return XMLOperation.create(node, path)
    }

    static YAMLFileHandler handle(String strYAML) {
        YAMLFileHandler handler = new YAMLFileHandler()
        Yaml parser = new Yaml()
        handler.node = ConvertUtils.convert(parser.load(strYAML))
        return handler
    }

    static YAMLFileHandler handle(File file) {
        YAMLFileHandler handler = handle(file.text)
        handler.file = file
        return handler
    }
}
