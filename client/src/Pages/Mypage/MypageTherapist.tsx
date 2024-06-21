import { useState, useEffect } from "react";

import Navigation from "../../Component/Mypage/Navigation/Navigation";
import MatchingContent from "../../Component/Mypage/TherapistContent/Matching/MatchingContent";
import ProfileContent from "../../Component/Mypage/TherapistContent/Profile/ProfileContent";
import ReviewContent from "../../Component/Mypage/TherapistContent/Review/ReviewContent";

import { StyledMypageLayout } from "../../Assets/StyledData/PageLayout.style";
import { clickButtonEvent } from "../../Assets/CommonType/EventType";

function MypageTherapist() {
  const [clickedNavMenu, setClickedNavMenu] = useState<string>(
    initialClickedNavMenu
  );

  const handleNavMenuClick = (e: clickButtonEvent) => {
    setClickedNavMenu(e.currentTarget.innerText);
  };
  function rootBackgroundColorChange() {
    return (document.body.style.backgroundColor = "#f2f2f2");
  }

  useEffect(() => {
    rootBackgroundColorChange();
  }, []);

  return (
    <StyledMypageLayout>
      <Navigation
        userType="therapist"
        handleNavMenu={handleNavMenuClick}
        clickedNavMenu={clickedNavMenu}
      />
      {handleContentByNavMenu(clickedNavMenu)}
    </StyledMypageLayout>
  );
}

export default MypageTherapist;

const initialClickedNavMenu: string = "내 정보";

const handleContentByNavMenu = (clickedNavMenu: string) => {
  switch (clickedNavMenu) {
    case "내 정보":
      return <ProfileContent />;
    case "매칭":
      return <MatchingContent />;
    case "리뷰":
      return <ReviewContent />;
    default:
      return <ProfileContent />;
  }
};
