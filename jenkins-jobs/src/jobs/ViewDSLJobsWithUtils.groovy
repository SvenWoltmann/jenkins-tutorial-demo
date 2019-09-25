import javaposse.jobdsl.dsl.View
import util.ViewUtils

View view = listView('Part II - DSL with Utils') {
    jobs {
        regex '.+\\(DSL with Utils\\).*'
    }
}

ViewUtils.addDefaultColumns(view)
