package util

import javaposse.jobdsl.dsl.View

class ViewUtils {

    static void addDefaultColumns(View view) {
        view.with {
            columns {
                status()
                weather()
                name()
                lastSuccess()
                lastFailure()
                lastDuration()
                buildButton()
            }
        }
    }

}
