/**
 * @author 김정호
 * 작성일: 2026-10-27
 */

/** 관리 화면의 제목과 오른쪽 버튼을 둔다. */
function PageShell({ title, actions, children }) {
  return (
    <div className="min-h-screen bg-background text-text">
      <div className="mx-auto max-w-5xl px-4 py-8">
        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-bold">{title}</h1>
          {actions}
        </div>
        {children}
      </div>
    </div>
  )
}
const inputClass = 'w-full rounded-lg border border-text/15 bg-white px-3 py-2 text-sm outline-none focus:border-main'
const primaryButton = 'rounded-lg bg-main px-4 py-2 text-sm font-semibold text-white hover:opacity-90 disabled:opacity-50'
const outlineButton = 'rounded-lg border border-main px-3 py-1.5 text-sm text-main hover:bg-main/10'
const dangerButton = 'rounded-lg border border-red-400 px-3 py-1.5 text-sm text-red-600 hover:bg-red-50'
/** 제목과 오른쪽 버튼이 있는 흰 구역. */
function Card({ title, children, actions }) {
  return (
    <section className="mt-6 rounded-xl bg-white p-5 shadow-sm">
      <div className="mb-3 flex items-center justify-between">
        <h2 className="text-lg font-bold">{title}</h2>
        {actions}
      </div>
      {children}
    </section>
  )
}
/** 입력 칸의 이름. 필수면 별표를 붙인다. */
function Field({ label, required, children }) {
  return (
    <label className="block text-sm">
      <span className="mb-1 block font-medium">
        {label} {required && <span className="text-accent">*</span>}
      </span>
      {children}
    </label>
  )
}
/** 로딩·빈 목록·오류처럼 본문 대신 보여 주는 문구. */
function Message({ children }) {
  return <p className="py-16 text-center text-sm text-text/60">{children}</p>
}
export { Card, Field, Message, PageShell, dangerButton, inputClass, outlineButton, primaryButton }