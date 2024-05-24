import styled from "styled-components";

// SystemBox
export const StyledChatboxSystemBox = styled.div`
  display: inline-block;
  background-color: #f2f2f2;
  padding: 16px;
  max-width: 256px;
  border-radius: 0 16px 16px 16px;

  & p {
    line-height: 24px;
    width: fit-content;
  }
  & span {
    color: #333;
  }
`;

export const StyledButtonWrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;

  & > div {
    width: 100%;
  }

  & > div button {
    justify-content: center;
  }

  &.gender_buttons > div:nth-child(1),
  &.gender_buttons > div:nth-child(2) {
    width: calc(50% - 4px);
  }
`;

export const StyledChatboxUserBox = styled(StyledChatboxSystemBox)`
  background-color: #333333;
  border-radius: 16px 16px 0 16px;
  & span {
    color: #fff;
  }
`;