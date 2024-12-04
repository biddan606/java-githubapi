package dev.biddan;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.Builder;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCommit.File;
import org.kohsuke.github.GHCommit.ShortInfo;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;

@Builder
public record Commit(
        String sha,
        CommitDetail detail,
        CommitAuthor author,
        List<CommitFile> files,
        LocalDateTime createAt
) {

    public static Commit from(GHCommit ghCommit, GHRepository ghRepository) {
        try {
            return Commit.builder()
                    .sha(ghCommit.getSHA1())
                    .detail(CommitDetail.from(ghCommit.getCommitShortInfo()))
                    .author(CommitAuthor.from(ghCommit.getAuthor()))
                    .files(ghCommit.listFiles().toList().stream()
                            .map(file -> CommitFile.from(file, ghRepository, ghCommit.getSHA1()))
                            .toList())
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

    @Builder
    public record CommitFile(
            String fileName,
            String status,
            int additions,
            int deletions,
            int changes,
            String patch,
            String content
    ) {

        public static CommitFile from(File file, GHRepository repository, String sha) {
            return CommitFile.builder()
                    .fileName(file.getFileName())
                    .status(file.getStatus())
                    .additions(file.getLinesAdded())
                    .deletions(file.getLinesDeleted())
                    .changes(file.getLinesChanged())
                    .patch(file.getPatch())
                    .content(getContent(file, repository, sha))
                    .build();
        }

        private static String getContent(File file, GHRepository repository, String sha) {
            try (InputStream inputStream = repository.getFileContent(file.getFileName(), sha).read()) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new GitHubException("파일 읽기 실패", e);
            }
        }
    }
}
