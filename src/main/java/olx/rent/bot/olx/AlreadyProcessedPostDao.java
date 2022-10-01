package olx.rent.bot.olx;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlreadyProcessedPostDao extends JpaRepository<AlreadyProcessedPost, String> {

    @Query(value = "select exists (select 1 from processed_post where link=:link)", nativeQuery = true)
    boolean checkIfPostProcessedByLink(@Param("link") String link);
}
