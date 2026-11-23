/**
 * @file 계약 문서 검토(상세) 화면 (검토 대기 계약의 반려·승인 처리)
 * @author 문경완
 * @since 2026-11-12
 * @version 1.0.0
 */

import { useEffect, useState } from 'react'
import { Building2 } from 'lucide-react'
import { approveContract, fetchContract, rejectContract } from '../api/contractApi'
import ContractStatusBadge from '../components/ContractStatusBadge'
import { formatDate, formatMoney, formatWon } from '../utils/contractFormat'

/* HTTP 상태 코드별 안내 문구 */
const ERROR_MESSAGE = {
    400: '입력값을 확인해 주세요.',
    404: '계약서를 찾을 수 없어요.',
    409: '이미 처리된 계약이에요. 목록으로 돌아가 최신 상태를 확인해 주세요.',
}

/* 사유 입력이 필요한 동작별 설정 (입력칸 문구와 호출할 API만 다름) */
const REASON_ACTIONS = {
    reject: {
        label: '반려 사유',
        placeholder: '신청자에게 전달할 반려 사유를 적어 주세요.',
        submit: '반려하기',
        done: '반려했어요.',
        send: rejectContract,
        submitStyle: 'bg-red-700 hover:bg-red-800',
    },
}

/* 상태 사유 카드 설정 (반려일 때만 사유를 보여줌) */
const REASON_CARD = {
    REJECTED: { title: '반려 사유', style: 'bg-red-50 text-red-700' },
}

/* 본문 미리보기 iframe의 CSP. 외부 이미지·글꼴·스타일 파일을 불러오지 못하게 막음 (신청자 입력값이 들어간 본문이라서, 본문 안 <style>만 허용) */
const PREVIEW_CSP = `<meta http-equiv="Content-Security-Policy" content="default-src 'none'; style-src 'unsafe-inline'; img-src data:">`

/* 본문 미리보기 안에 넣는 글꼴·표 모양 (iframe 안은 화면의 CSS가 닿지 않아서 따로 넣음) */
const PREVIEW_STYLE = `<style>
body{margin:0;padding:16px;font-family:'Noto Sans KR',system-ui,sans-serif;font-size:14px;line-height:1.7;color:#1f2a27}
h1{font-size:22px;margin:0 0 12px}h2{font-size:18px;margin:16px 0 8px}h3{font-size:16px;margin:12px 0 6px}
p{margin:0 0 8px}ul{list-style:disc;padding-left:24px}ol{list-style:decimal;padding-left:24px}
table{width:100%;border-collapse:collapse}th,td{border:1px solid #cbd5d0;padding:6px 10px}th{background:#f3f5f1}
blockquote{margin:8px 0;padding-left:12px;border-left:3px solid #52796f;color:#5b6b65}hr{border:0;border-top:1px solid #cbd5d0}
</style>`

/**
 * 이름과 값이 한 줄로 나란한 정보 한 줄
 * @param {Object} props
 * @param {string} props.label - 항목 이름
 * @param {string} props.value - 항목 값 (없으면 "-")
 * @returns {JSX.Element}
 */
function InfoRow({ label, value }) {
    return (
        <div className="flex justify-between gap-4 py-2 text-sm">
            <dt className="shrink-0 text-text/60">{label}</dt>
            <dd className="text-right font-medium">{value || '-'}</dd>
        </div>
    )
}

/**
 * 제목이 붙은 정보 카드
 * @param {Object} props
 * @param {string} props.title - 카드 제목
 * @param {React.ReactNode} props.children - 카드 안에 넣을 정보 줄
 * @returns {JSX.Element}
 */
function InfoCard({ title, children }) {
    return (
        <section className="rounded-lg bg-white p-4 shadow-sm">
            <h2 className="text-sm font-semibold text-main">{title}</h2>
            <dl className="mt-2 divide-y divide-main/10">{children}</dl>
        </section>
    )
}

/**
 * 문서 검토(상세) 화면. 검토 대기 계약만 반려·승인 버튼이 보임
 * @param {Object} props
 * @param {number} props.applicationId - 신청 ID
 * @param {Function} props.onClose - 목록으로 돌아갈 때 호출
 * @returns {JSX.Element}
 */
function ContractReviewPage({ applicationId, onClose }) {
    /* 계약 상세 데이터 */
    const [detail, setDetail] = useState(null)
    /* 불러오기 상태 (loading | done | notfound | error) */
    const [status, setStatus] = useState('loading')
    /* 요청 처리 중인지 여부 */
    const [busy, setBusy] = useState(false)
    /* 사유 입력칸이 열린 동작 (null | reject) */
    const [action, setAction] = useState(null)
    /* 입력한 사유 */
    const [reason, setReason] = useState('')
    /* 처리 성공 안내 문구 */
    const [notice, setNotice] = useState('')
    /* 오류 안내 문구 */
    const [error, setError] = useState('')

    /* 신청 ID가 바뀌면 계약 상세를 불러옴 */
    useEffect(() => {
        let cancelled = false
        /* 계약 상세를 조회해 상태에 반영 */
        async function load() {
            try {
                const data = await fetchContract(applicationId)
                if (!cancelled) {
                    setDetail(data)
                    setStatus('done')
                }
            } catch (e) {
                if (!cancelled) setStatus(e.status === 404 ? 'notfound' : 'error')
            }
        }
        load()
        return () => {
            cancelled = true
        }
    }, [applicationId])

    /* 요청을 보내고 서버가 돌려준 바뀐 상세로 화면 갱신 */
    async function run(request, doneMessage) {
        setBusy(true)
        setError('')
        setNotice('')
        try {
            const updated = await request()
            setDetail(updated)
            setNotice(doneMessage)
            setAction(null)
            setReason('')
        } catch (e) {
            setError(ERROR_MESSAGE[e.status] ?? '요청에 실패했어요. 잠시 후 다시 시도해 주세요.')
        } finally {
            setBusy(false)
        }
    }

    /* 반려 사유 입력칸을 열거나 닫음 (다른 동작이 열려 있었다면 입력하던 내용은 비움) */
    function openReasonForm(next) {
        setAction(next)
        setReason('')
        setError('')
    }

    /* 확인을 받은 뒤 계약 승인 */
    function handleApprove() {
        if (window.confirm('이 계약을 승인할까요?')) {
            run(() => approveContract(applicationId), '승인했어요.')
        }
    }

    /* 입력한 사유로 반려 요청 */
    function handleSubmitReason() {
        const current = REASON_ACTIONS[action]
        run(() => current.send(applicationId, reason.trim()), current.done)
    }

    const backButton = (
        <button type="button" onClick={onClose} className="text-sm text-main hover:underline">
            ← 목록으로
        </button>
    )

    if (status !== 'done') {
        return (
            <>
                {backButton}
                <p className={`py-10 text-center text-sm ${status === 'loading' ? 'text-text/70' : 'text-red-700'}`}>
                    {status === 'loading' && '불러오는 중이에요.'}
                    {status === 'notfound' && ERROR_MESSAGE[404]}
                    {status === 'error' && '계약서를 불러오지 못했어요.'}
                </p>
            </>
        )
    }

    const reasonCard = detail.statusReason ? REASON_CARD[detail.statusGroup] : null
    const reasonForm = action ? REASON_ACTIONS[action] : null

    return (
        <div className="max-w-5xl">
            {backButton}
            <h1 className="mt-2 text-2xl font-semibold text-main sm:text-3xl">계약 문서 상세</h1>

            <section className="mt-6 rounded-lg bg-white p-5 shadow-sm">
                <div className="flex items-start justify-between gap-4">
                    <div>
                        <div className="flex items-center gap-2">
                            <span className="text-xl font-semibold">{detail.applicantName}</span>
                            <ContractStatusBadge group={detail.statusGroup} label={detail.statusLabel} />
                        </div>
                        <p className="mt-2 flex items-center gap-1.5 text-sm text-text/70">
                            <Building2 size={14} />
                            {detail.buildingName} {detail.roomName}
                        </p>
                    </div>
                    <div className="text-right text-xs text-text/60">
                        <p>계약 ID #{detail.applicationId}</p>
                        <p className="mt-0.5">문서 ID {detail.documentId ? `#${detail.documentId}` : '-'}</p>
                        <p className="mt-0.5">{detail.applicationNo}</p>
                    </div>
                </div>
                <div className="mt-4 grid gap-3 border-t border-main/10 pt-4 text-sm sm:grid-cols-2">
                    <div>
                        <span className="block text-xs text-text/60">계약 기간</span>
                        <span className="font-medium">
                            {formatDate(detail.moveInDate)} ~ {formatDate(detail.moveOutDate)}
                        </span>
                    </div>
                    <div>
                        <span className="block text-xs text-text/60">월세 / 보증금</span>
                        <span className="font-medium">
                            {formatMoney(detail.monthlyFee)} / {formatMoney(detail.deposit)}
                        </span>
                    </div>
                </div>
            </section>

            <div className="mt-4 grid gap-4 md:grid-cols-2">
                <InfoCard title="계약자 정보">
                    <InfoRow label="이름" value={detail.applicantName} />
                    <InfoRow label="이메일" value={detail.applicantEmail} />
                    <InfoRow label="휴대전화" value={detail.applicantPhone} />
                    <InfoRow label="주소" value={detail.applicantAddress} />
                    <InfoRow label="유저 ID" value={`#${detail.memberId}`} />
                </InfoCard>
                <InfoCard title="매물 정보">
                    <InfoRow label="건물명" value={detail.buildingName} />
                    <InfoRow label="호실" value={detail.roomName} />
                </InfoCard>
            </div>

            <div className="mt-4">
                <InfoCard title="계약 조건">
                    <InfoRow label="입주일" value={formatDate(detail.moveInDate)} />
                    <InfoRow label="퇴실 예정일" value={formatDate(detail.moveOutDate)} />
                    <InfoRow label="계약 기간" value={`${detail.contractMonths}개월`} />
                    <InfoRow label="월 이용료" value={formatWon(detail.monthlyFee)} />
                    <InfoRow
                        label="장기 계약 할인"
                        value={detail.discountAmount > 0 ? `-${formatWon(detail.discountAmount)}` : ''}
                    />
                    <InfoRow label="보증금" value={formatWon(detail.deposit)} />
                    <InfoRow label="첫 결제 금액" value={formatWon(detail.firstPayment)} />
                </InfoCard>
            </div>

            <section className="mt-4 rounded-lg bg-white p-4 shadow-sm">
                <h2 className="text-sm font-semibold text-main">계약서 본문</h2>
                {/* 신청자가 입력한 값이 들어간 HTML이라, 스크립트가 못 도는 샌드박스 안에서만 보여준다. */}
                <iframe
                    title="계약서 본문"
                    sandbox=""
                    srcDoc={`${PREVIEW_CSP}${PREVIEW_STYLE}${detail.content ?? ''}`}
                    className="mt-3 h-112 w-full rounded-lg border border-main/20 bg-white"
                />
            </section>

            {reasonCard && (
                <section className={`mt-4 rounded-lg p-4 text-sm ${reasonCard.style}`}>
                    <p className="font-semibold">{reasonCard.title}</p>
                    <p className="mt-1">{detail.statusReason}</p>
                </section>
            )}

            {notice && <p className="mt-4 rounded-lg bg-main/10 px-4 py-2 text-sm text-main">{notice}</p>}
            {error && <p className="mt-4 rounded-lg bg-red-50 px-4 py-2 text-sm text-red-700">{error}</p>}

            {detail.reviewable && (
                <>
                    {reasonForm && (
                        <section className="mt-4 rounded-lg bg-white p-4 shadow-sm">
                            <label className="text-sm font-medium">{reasonForm.label}</label>
                            <textarea
                                value={reason}
                                onChange={(e) => setReason(e.target.value)}
                                maxLength={500}
                                rows={3}
                                placeholder={reasonForm.placeholder}
                                className="mt-2 w-full rounded-lg border border-main/20 px-4 py-2.5 text-sm outline-none focus:border-main"
                            />
                            <div className="mt-3 grid grid-cols-2 gap-3">
                                <button
                                    type="button"
                                    onClick={() => openReasonForm(null)}
                                    disabled={busy}
                                    className="cursor-pointer rounded-lg border border-main/30 px-4 py-2.5 text-sm hover:bg-background"
                                >
                                    취소
                                </button>
                                <button
                                    type="button"
                                    onClick={handleSubmitReason}
                                    disabled={busy || !reason.trim()}
                                    className={`cursor-pointer rounded-lg px-4 py-2.5 text-sm font-medium text-white disabled:opacity-40 ${reasonForm.submitStyle}`}
                                >
                                    {reasonForm.submit}
                                </button>
                            </div>
                        </section>
                    )}

                    {/* 반려 사유 입력칸이 열려 있으면 그 안의 취소·반려하기 버튼만 보여준다 (반려·승인 버튼 중복 방지) */}
                    {!reasonForm && (
                    <div className="mt-4 grid grid-cols-2 gap-3">
                        <button
                            type="button"
                            onClick={() => openReasonForm('reject')}
                            disabled={busy}
                            className={`cursor-pointer rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700 hover:bg-red-100 disabled:opacity-40 ${
                                action === 'reject' ? 'ring-2 ring-red-300' : ''
                            }`}
                        >
                            반려
                        </button>
                        <button
                            type="button"
                            onClick={handleApprove}
                            disabled={busy}
                            className="cursor-pointer rounded-lg bg-main px-4 py-3 text-sm font-medium text-background hover:bg-main/90 disabled:opacity-40"
                        >
                            승인
                        </button>
                    </div>
                    )}
                </>
            )}
        </div>
    )
}

export default ContractReviewPage