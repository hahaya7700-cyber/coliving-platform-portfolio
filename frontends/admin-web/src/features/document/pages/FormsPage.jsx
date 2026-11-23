/**
 * @file 계약서 양식 목록 화면 (검색, 사전 양식으로 생성, 사용 중 양식 표시)
 * @author 문경완
 * @since 2026-11-12
 * @version 1.0.0
 */

import { useCallback, useEffect, useMemo, useState } from 'react'
import { useLocation } from 'react-router'
import { ChevronRight } from 'lucide-react'
import { fetchForms } from '../api/formApi'
import FormEditor from '../components/FormEditor'
import { PRESET_FORMS } from '../utils/presetForms'

/* 양식 언어별 표시 라벨 */
const LANGUAGE_LABEL = { ko: '한글', en: '영어' }

/**
 * ISO 시각을 YYYY.MM.DD 로 표시
 * @param {string} iso - ISO-8601 시각 문자열
 * @returns {string} YYYY.MM.DD 형식의 날짜
 */
function formatDate(iso) {
    const date = new Date(iso)
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${date.getFullYear()}.${month}.${day}`
}

/**
 * 계약 서비스가 언어별로 실제 쓰는 양식의 id 집합 계산. 계약 서비스(DocumentQueryService.getLatestFormForContract)와 같은 규칙: 같은 언어, 본문이 비어 있지 않은 양식 중 id가 가장 큰 것
 * @param {Array} forms - 전체 양식 목록
 * @returns {Set<number>} 사용 중인 양식 id 집합
 */
function findActiveFormIds(forms) {
    const active = new Set()
    for (const language of Object.keys(LANGUAGE_LABEL)) {
        let latest = null
        for (const form of forms) {
            if (form.language !== language) continue
            if (!form.content || !form.content.trim()) continue
            if (latest == null || form.id > latest.id) latest = form
        }
        if (latest != null) active.add(latest.id)
    }
    return active
}

/**
 * 양식 목록 화면 (편집 중이면 FormEditor로 전환)
 * @param {Object} props
 * @param {string} props.title - 화면 제목
 * @returns {JSX.Element}
 */
function FormsView({ title }) {
    /* 양식 목록 */
    const [forms, setForms] = useState([])
    /* 양식 이름 검색어 */
    const [keyword, setKeyword] = useState('')
    /* 불러오기 상태 (loading | done | error) */
    const [status, setStatus] = useState('loading')
    /* 편집 상태 (null: 목록, "new": 새 양식, 양식 객체: 수정) */
    const [editing, setEditing] = useState(null)
    /* 사전 양식으로 생성할 때 채울 값 */
    const [preset, setPreset] = useState(null)

    /* 양식 목록을 불러와 상태에 반영 */
    const load = useCallback(async () => {
        try {
            setForms(await fetchForms())
            setStatus('done')
        } catch {
            setStatus('error')
        }
    }, [])

    /* 화면 제목을 반영하고 양식 목록을 불러옴 */
    useEffect(() => {
        document.title = title
        load()
    }, [title, load])

    /* 사용 중 양식 id 집합 (검색으로 걸러지기 전의 전체 목록으로 계산하므로 검색어에 따라 배지가 바뀌지 않음) */
    const activeFormIds = useMemo(() => findActiveFormIds(forms), [forms])

    /* 새 양식 화면을 연다. nextPreset이 null이면 빈 양식, 값이 있으면 그 문안으로 채움 */
    function startCreate(nextPreset) {
        setPreset(nextPreset)
        setEditing('new')
    }

    if (editing) {
        return (
            <FormEditor
                form={editing === 'new' ? null : editing}
                preset={preset}
                onClose={() => setEditing(null)}
                onSaved={() => {
                    setEditing(null)
                    load()
                }}
            />
        )
    }

    const shown = forms.filter((form) => form.title.toLowerCase().includes(keyword.trim().toLowerCase()))

    return (
        <div className="max-w-5xl">
            <div className="flex items-start justify-between gap-4">
                <div>
                    <h1 className="text-2xl font-semibold text-main sm:text-3xl">{title}</h1>
                    <p className="mt-1 text-sm text-text/70">계약서 양식을 관리해요.</p>
                </div>
                <button
                    type="button"
                    onClick={() => startCreate(null)}
                    className="cursor-pointer rounded-lg bg-main px-4 py-2 text-sm font-medium text-background shadow-sm transition duration-200 hover:bg-main/90 hover:shadow-md focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-main active:scale-95"
                >
                    + 생성
                </button>
            </div>

            <section className="mt-6 rounded-lg bg-white p-4 shadow-sm">
                <p className="text-sm font-medium">사전 양식으로 생성</p>
                <div className="mt-3 grid grid-cols-2 gap-2">
                    {PRESET_FORMS.map((item) => (
                        <button
                            key={item.language}
                            type="button"
                            onClick={() => startCreate(item)}
                            className="cursor-pointer rounded-lg border border-main/20 bg-background px-4 py-2.5 text-sm transition duration-200 hover:border-main hover:bg-main/10 hover:text-main focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-main active:scale-[0.98]"
                        >
                            {item.label}
                        </button>
                    ))}
                </div>
            </section>

            <input
                type="search"
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                placeholder="양식 이름 검색"
                className="mt-4 w-full rounded-lg border border-main/20 bg-white px-4 py-2.5 text-sm outline-none transition-colors hover:border-main/50 focus:border-main"
            />

            <p className="mt-3 text-xs text-text/60">
                '사용 중' 표시는 새 계약서에 실제로 쓰이는 양식이에요. 언어별로 가장 나중에 등록한 양식이 쓰이고, 예전 양식을 고쳐도 바뀌지 않아요.
            </p>

            <section className="mt-3 space-y-3">
                {status === 'loading' && <p className="py-10 text-center text-sm text-text/70">불러오는 중이에요.</p>}
                {status === 'error' && (
                    <p className="py-10 text-center text-sm text-red-700">양식을 불러오지 못했어요.</p>
                )}
                {status === 'done' && shown.length === 0 && (
                    <p className="py-10 text-center text-sm text-text/70">표시할 양식이 없어요.</p>
                )}
                {shown.map((form) => (
                    <button
                        key={form.id}
                        type="button"
                        onClick={() => setEditing(form)}
                        className="group flex w-full cursor-pointer items-center justify-between rounded-lg border border-transparent bg-white p-4 text-left shadow-sm transition duration-200 ease-out hover:-translate-y-0.5 hover:border-main/40 hover:shadow-lg focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-main active:translate-y-0 active:shadow-sm motion-reduce:transition-none motion-reduce:hover:translate-y-0"
                    >
                        <div>
                            <div className="flex items-center gap-2">
                                <span className="font-medium transition-colors group-hover:text-main">
                                    {form.title}
                                </span>
                                <span className="rounded bg-main/10 px-2 py-0.5 text-xs text-main">
                                    {LANGUAGE_LABEL[form.language]}
                                </span>
                                {activeFormIds.has(form.id) && (
                                    <span className="rounded bg-main px-2 py-0.5 text-xs text-background">
                                        사용 중
                                    </span>
                                )}
                            </div>
                            <p className="mt-1 text-xs text-text/60">최종 수정 {formatDate(form.updatedAt)}</p>
                        </div>
                        <ChevronRight
                            size={18}
                            className="text-text/40 transition duration-200 group-hover:translate-x-1 group-hover:text-main"
                        />
                    </button>
                ))}
            </section>
        </div>
    )
}

/**
 * 양식 페이지. 사이드바에서 같은 메뉴를 다시 누르면 location.key가 바뀌므로, 그 값을 key로 줘서 화면을 새로 만들고 목록으로 돌아가게 함
 * @param {Object} props - 화면에 그대로 넘기는 속성
 * @returns {JSX.Element}
 */
function FormsPage(props) {
    /* 현재 라우터 위치 (같은 메뉴를 다시 눌렀는지 알려 주는 key 포함) */
    const location = useLocation()
    return <FormsView key={location.key} {...props} />
}

export default FormsPage