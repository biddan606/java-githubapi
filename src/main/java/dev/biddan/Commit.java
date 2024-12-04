package dev.biddan;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.Builder;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCommit.ShortInfo;
import org.kohsuke.github.GHUser;

@Builder
public record Commit(String sha, CommitDetail detail, CommitAuthor author, LocalDateTime createAt) {

    public static Commit from(GHCommit ghCommit) {
        try {
            return Commit.builder()
                    .sha(ghCommit.getSHA1())
                    .detail(CommitDetail.from(ghCommit.getCommitShortInfo()))
                    .author(CommitAuthor.from(ghCommit.getAuthor()))
                    .createAt(ghCommit.getCommitShortInfo().getAuthoredDate().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime())
                    .build();
        } catch (IOException e) {
            throw new GitHubException("커밋 변환 중 오류 발생", e);
        }
    }

    @Builder
    public record CommitDetail(String message, String url) {

        public static CommitDetail from(ShortInfo shortInfo) {
            return CommitDetail.builder()
                    .message(shortInfo.getMessage())
                    .url(shortInfo.getUrl())
                    .build();
        }
    }

    @Builder
    public record CommitAuthor(String name, String email) {

        public static CommitAuthor from(GHUser author) {
            try {
                return CommitAuthor.builder()
                        .name(author.getName())
                        .email(author.getEmail())
                        .build();
            } catch (IOException e) {
                throw new GitHubException("커밋 작성자 정보 변환 중 오류 발생", e);
            }
        }
    }
}
