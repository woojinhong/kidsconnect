import { Highlight } from "@mantine/core";
import OutlineButton from "../Button/OutlineButton";

import {
  StyledChatboxSystemBox,
  StyledButtonWrapper,
  StyledAnimationContainer,
} from "./Chatbox.style";
import LoadingAnimation from "../../../Assets/Animation/Loading.gif";

type ChatboxSystemProps = {
  highlightWords?: string;
  messages?: string;
  button?: string[];
  animation?: boolean;
};

function ChatboxSystem({
  highlightWords = "",
  messages = "",
  button = [],
  animation = false,
}: ChatboxSystemProps) {
  return (
    <StyledChatboxSystemBox>
      <Highlight
        highlight={highlightWords}
        color="transparent"
        highlightStyles={{
          fontWeight: 700,
        }}
      >
        {messages}
      </Highlight>
      {button ? (
        <StyledButtonWrapper
          className={messages.includes("선생님 성별") ? "gender_buttons" : ""}
        >
          {button.map((text) => {
            return (
              <OutlineButton
                key={text}
                text={text}
                variant="m_outline"
                borderColor="#c1c1c1"
              ></OutlineButton>
            );
          })}
        </StyledButtonWrapper>
      ) : null}
      {animation ? (
        <StyledAnimationContainer>
          <img src={LoadingAnimation} alt="로딩 중" />
        </StyledAnimationContainer>
      ) : null}
    </StyledChatboxSystemBox>
  );
}

export default ChatboxSystem;
