package patches.projects

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the project with id = 'PullRequests_2'
accordingly, and delete the patch script.
*/
changeProject(RelativeId("PullRequests_2")) {
    params {
        expect {
            param("VCS.Branches", """
                -:refs/heads/*
                +:refs/pull/(*)/head
                -:refs/heads/(CI/*)
            """.trimIndent())
        }
        update {
            param("VCS.Branches", """
                -:refs/heads/*
                +:refs/pull/(*)/head
                -:refs/heads/(CI/*)
                -:<default>
            """.trimIndent())
        }
    }
}