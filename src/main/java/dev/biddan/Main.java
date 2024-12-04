package dev.biddan;

import java.io.IOException;
import org.kohsuke.github.GHRepository;

public class Main {
    private static final String GITHUB_TOKEN = "GITHUB_TOKEN";

    public static void main(String[] args) throws IOException {
        // 깃허브 토큰 설정
        String oauthToken = System.getenv(GITHUB_TOKEN);

        // 깃허브 클라이언트 연결
        System.out.println(oauthToken);
        GithubClient client = new GithubClient(oauthToken);

        // 레포지토리 가져오기
        Repository repository = new Repository("biddan606", "coding-tests");
        GHRepository githubRepository = client.getRepository(repository);

        // 레포지토리 전체 커밋 조회
    }
}
