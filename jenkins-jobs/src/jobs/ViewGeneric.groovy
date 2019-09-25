import javaposse.jobdsl.dsl.View
import util.ViewUtils

View view = listView('Part III - Generic') {
    jobs {
        regex '(Jenkins Tutorial Demo - Build |Jenkins Tutorial Demo - Release ).*'
    }
}

ViewUtils.addDefaultColumns(view)
