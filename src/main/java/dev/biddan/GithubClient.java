package dev.biddan;

import java.io.IOException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

public class GithubClient {

    private final GitHub gitHub;

    public GithubClient(String oauthToken) {
        try {
            this.gitHub = new GitHubBuilder()
                    .withOAuthToken(oauthToken)
                    .build();
        } catch (IOException e) {
            throw new GitHubException("깃허브 클라이언트 생성 실패");
        }
    }

    public GHRepository getRepository(Repository repository) {
        try {
            return gitHub.getRepository(repository.getFullName());
        } catch (IOException e) {
            throw new GitHubException("저장소 조회 실패: " + repository.getFullName(), e);
        }
    }
}
