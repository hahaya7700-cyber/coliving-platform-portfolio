/**
 * @author 김정호
 * 작성일: 2026-10-27
 */

/** 금액을 언어에 맞게 보여 준다. 한국어는 만원 단위로 줄인다. */
function formatWon(value, locale) {
  if (value === null || value === void 0) return '-'
  if (locale === 'en') return `₩${value.toLocaleString('en-US')}`
  if (value === 0) return '0원'
  if (value % 1e4 === 0) return `${(value / 1e4).toLocaleString('ko-KR')}만원`
  return `${value.toLocaleString('ko-KR')}원`
}
/** 숫자를 언어에 맞는 자릿수로 보여 준다. */
function formatNumber(value, locale) {
  return value.toLocaleString(locale === 'en' ? 'en-US' : 'ko-KR')
}
/** 임대 유형 코드를 화면 문구로 바꾼다. 없으면 대시. */
function leaseTypeLabel(code, t) {
  return code ? t(`leaseTypes.${code}`, { defaultValue: code }) : '-'
}
/** 방 종류를 화면 문구로 바꾼다. 없으면 대시. */
function roomTypeLabel(type, t) {
  return type ? t(`roomTypes.${type}`, { defaultValue: type }) : '-'
}
/** 참·거짓을 예 또는 아니오로 보여 준다. */
function formatYesNo(value, t) {
  return value ? t('common.yes') : t('common.no')
}
/** 요청 실패를 상태 코드가 있으면 그 문구로, 없으면 통신 오류로 보여 준다. */
function errorMessage(error, t) {
  return error && error.status ? t('errors.requestFailed', { status: error.status }) : t('errors.network')
}
/** 편의시설을 종류별로 묶는다. */
function groupByCategory(items) {
  const map = /* @__PURE__ */ new Map()
  items.forEach((item) => {
    map.set(item.category, [...(map.get(item.category) ?? []), item])
  })
  return map
}
export { errorMessage, formatNumber, formatWon, formatYesNo, groupByCategory, leaseTypeLabel, roomTypeLabel }