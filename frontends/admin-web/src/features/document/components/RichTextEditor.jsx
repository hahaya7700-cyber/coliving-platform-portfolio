/**
 * @file 양식 본문 리치 텍스트 에디터 (서식 도구 모음, HTML 소스 보기, 빈칸 삽입)
 * @author 문경완
 * @since 2026-11-12
 * @version 1.0.0
 */

import { useCallback, useEffect, useRef, useState } from 'react'
import { EditorContent, useEditor } from '@tiptap/react'
import StarterKit from '@tiptap/starter-kit'
import TextAlign from '@tiptap/extension-text-align'
import { TableKit } from '@tiptap/extension-table'
import {
    AlignCenter,
    AlignLeft,
    AlignRight,
    Bold,
    Code,
    Italic,
    List,
    ListOrdered,
    Minus,
    Redo2,
    Strikethrough,
    Table as TableIcon,
    Underline as UnderlineIcon,
    Undo2,
} from 'lucide-react'
import { FORM_VARIABLES } from '../utils/formVariables'
import './FormEditor.css'

/* 에디터 확장 목록. 컴포넌트 밖에 한 번만 만듦 (렌더링마다 새로 만들면 에디터가 옵션이 바뀐 줄 알고 계속 갱신함) */
const EXTENSIONS = [StarterKit, TextAlign.configure({ types: ['heading', 'paragraph'] }), TableKit]

/* 에디터 영역에 붙일 속성 (CSS 클래스) */
const EDITOR_PROPS = { attributes: { class: 'form-editor-content' } }

/* 문단 스타일 드롭다운 항목 */
const STYLE_OPTIONS = [
    { value: 'p', label: '본문' },
    { value: 'h1', label: '제목 1' },
    { value: 'h2', label: '제목 2' },
    { value: 'h3', label: '제목 3' },
]

/**
 * 커서가 있는 곳의 문단 스타일 반환
 * @param {Object} editor - tiptap 에디터
 * @returns {string} p | h1 | h2 | h3
 */
function currentStyle(editor) {
    for (const level of [1, 2, 3]) {
        if (editor.isActive('heading', { level })) return `h${level}`
    }
    return 'p'
}

/**
 * 아이콘 버튼 하나 (마우스를 올리면 title이 설명으로 뜸)
 * @param {Object} props
 * @param {Function} props.icon - 아이콘 컴포넌트
 * @param {string} props.title - 버튼 설명
 * @param {boolean} props.active - 눌림 표시 여부
 * @param {boolean} props.disabled - 비활성 여부
 * @param {Function} props.onClick - 클릭 시 호출
 * @returns {JSX.Element}
 */
function Btn({ icon: Icon, title, active = false, disabled = false, onClick }) {
    return (
        <button
            type="button"
            title={title}
            aria-label={title}
            onClick={onClick}
            disabled={disabled}
            className={`flex h-8 w-8 items-center justify-center rounded ${
                active ? 'bg-main text-background' : 'hover:bg-main/10'
            } disabled:opacity-30`}
        >
            <Icon size={18} strokeWidth={2} />
        </button>
    )
}

/**
 * 표 도구 줄에서 쓰는 글자 버튼
 * @param {Object} props
 * @param {string} props.label - 버튼 글자
 * @param {boolean} props.danger - 삭제 등 위험 동작 표시 여부
 * @param {Function} props.onClick - 클릭 시 호출
 * @returns {JSX.Element}
 */
function TextBtn({ label, danger = false, onClick }) {
    return (
        <button
            type="button"
            onClick={onClick}
            className={`rounded px-2 py-1 text-xs ${
                danger ? 'text-red-700 hover:bg-red-50' : 'hover:bg-main/10'
            }`}
        >
            {label}
        </button>
    )
}

/**
 * 버튼 묶음 사이의 세로 구분선
 * @returns {JSX.Element}
 */
function Divider() {
    return <span className="mx-1 h-5 w-px bg-main/20" />
}

/**
 * 에디터 위 도구 모음 (소스 보기 중에는 서식 버튼을 막음)
 * @param {Object} props
 * @param {Object} props.editor - tiptap 에디터
 * @param {boolean} props.sourceMode - HTML 소스 보기 모드 여부
 * @param {Function} props.onToggleSource - 소스 보기 전환 시 호출
 * @returns {JSX.Element}
 */
function Toolbar({ editor, sourceMode, onToggleSource }) {
    /* 버튼을 눌러도 커서와 선택이 풀리지 않게 focus()와 함께 에디터 명령 실행 */
    const run = (command) => () => command(editor.chain().focus()).run()
    const off = sourceMode

    /* 문단 스타일 드롭다운 변경을 에디터에 적용 */
    function changeStyle(e) {
        const value = e.target.value
        const chain = editor.chain().focus()
        if (value === 'p') chain.setParagraph().run()
        else chain.setHeading({ level: Number(value.slice(1)) }).run()
    }

    return (
        <div className="border-b border-main/20 bg-background/60">
            <div className="flex flex-wrap items-center gap-0.5 p-2">
                <Btn icon={Undo2} title="되돌리기" disabled={off || !editor.can().undo()} onClick={run((c) => c.undo())} />
                <Btn icon={Redo2} title="다시 실행" disabled={off || !editor.can().redo()} onClick={run((c) => c.redo())} />
                <Divider />
                <select
                    value={currentStyle(editor)}
                    onChange={changeStyle}
                    disabled={off}
                    className="h-8 rounded border border-main/20 bg-white px-2 text-sm disabled:opacity-40"
                >
                    {STYLE_OPTIONS.map((item) => (
                        <option key={item.value} value={item.value}>
                            {item.label}
                        </option>
                    ))}
                </select>
                <Divider />
                <Btn icon={Bold} title="굵게" active={editor.isActive('bold')} disabled={off} onClick={run((c) => c.toggleBold())} />
                <Btn icon={Italic} title="기울임" active={editor.isActive('italic')} disabled={off} onClick={run((c) => c.toggleItalic())} />
                <Btn icon={UnderlineIcon} title="밑줄" active={editor.isActive('underline')} disabled={off} onClick={run((c) => c.toggleUnderline())} />
                <Btn icon={Strikethrough} title="취소선" active={editor.isActive('strike')} disabled={off} onClick={run((c) => c.toggleStrike())} />
                <Divider />
                <Btn icon={AlignLeft} title="왼쪽 정렬" active={editor.isActive({ textAlign: 'left' })} disabled={off} onClick={run((c) => c.setTextAlign('left'))} />
                <Btn icon={AlignCenter} title="가운데 정렬" active={editor.isActive({ textAlign: 'center' })} disabled={off} onClick={run((c) => c.setTextAlign('center'))} />
                <Btn icon={AlignRight} title="오른쪽 정렬" active={editor.isActive({ textAlign: 'right' })} disabled={off} onClick={run((c) => c.setTextAlign('right'))} />
                <Divider />
                <Btn icon={List} title="글머리 기호 목록" active={editor.isActive('bulletList')} disabled={off} onClick={run((c) => c.toggleBulletList())} />
                <Btn icon={ListOrdered} title="번호 목록" active={editor.isActive('orderedList')} disabled={off} onClick={run((c) => c.toggleOrderedList())} />
                <Divider />
                <Btn icon={Minus} title="구분선" disabled={off} onClick={run((c) => c.setHorizontalRule())} />
                <Btn icon={TableIcon} title="표 넣기" disabled={off} onClick={run((c) => c.insertTable({ rows: 3, cols: 3, withHeaderRow: true }))} />
                <span className="ml-auto" />
                <Btn icon={Code} title="HTML 소스 보기" active={sourceMode} onClick={onToggleSource} />
            </div>

            {/* 표 안에 커서가 있을 때만 나타나는 표 도구 줄 */}
            {editor.isActive('table') && !off && (
                <div className="flex flex-wrap items-center gap-1 border-t border-main/10 bg-white px-2 py-1.5">
                    <span className="mr-1 text-xs text-text/60">표</span>
                    <TextBtn label="행 추가" onClick={run((c) => c.addRowAfter())} />
                    <TextBtn label="행 삭제" onClick={run((c) => c.deleteRow())} />
                    <TextBtn label="열 추가" onClick={run((c) => c.addColumnAfter())} />
                    <TextBtn label="열 삭제" onClick={run((c) => c.deleteColumn())} />
                    <TextBtn label="표 삭제" danger onClick={run((c) => c.deleteTable())} />
                </div>
            )}
        </div>
    )
}

/**
 * 양식 본문 에디터 (고칠 때마다 onChange(html, 비어있는지) 호출)
 * @param {Object} props
 * @param {string} props.initialContent - 처음 보여줄 HTML
 * @param {Function} props.onChange - 내용이 바뀔 때 호출
 * @returns {JSX.Element}
 */
function RichTextEditor({ initialContent, onChange }) {
    /* HTML 소스 보기 모드 여부 */
    const [sourceMode, setSourceMode] = useState(false)
    /* 소스 보기에서 편집 중인 HTML */
    const [sourceText, setSourceText] = useState('')

    /* 최신 onChange를 담아 두는 ref (onChange가 바뀌어도 에디터 옵션은 그대로 두려고 사용) */
    const onChangeRef = useRef(onChange)
    /* onChange가 바뀌면 ref를 최신 함수로 갱신 */
    useEffect(() => {
        onChangeRef.current = onChange
    }, [onChange])

    /* 에디터 내용이 바뀔 때 HTML과 빈 문서 여부를 onChange로 전달 */
    const handleUpdate = useCallback(({ editor: current }) => {
        onChangeRef.current(current.getHTML(), current.isEmpty)
    }, [])

    /* 에디터 인스턴스 생성 */
    const editor = useEditor({
        extensions: EXTENSIONS,
        content: initialContent,
        shouldRerenderOnTransaction: true, // 커서가 움직일 때도 도구 모음의 눌림 표시를 갱신한다
        editorProps: EDITOR_PROPS,
        onUpdate: handleUpdate,
    })

    /* 에디터와 HTML 소스 보기 사이를 전환 (소스에서 돌아올 때 지원하지 않는 태그는 정리됨) */
    function toggleSource() {
        if (sourceMode) {
            editor.commands.setContent(sourceText)
            onChange(editor.getHTML(), editor.isEmpty)
            setSourceMode(false)
        } else {
            setSourceText(editor.getHTML())
            setSourceMode(true)
        }
    }

    /* 소스 보기에서 HTML을 고치면 바로 onChange로 전달 */
    function handleSourceChange(e) {
        setSourceText(e.target.value)
        onChange(e.target.value, !e.target.value.trim())
    }

    /* 커서 위치에 {{빈칸}} 삽입 */
    function insertVariable(key) {
        editor.chain().focus().insertContent(`{{${key}}}`).run()
    }

    return (
        <div className="mt-2 flex flex-col gap-3 lg:flex-row">
            <div className="min-w-0 flex-1 overflow-hidden rounded-lg border border-main/20 bg-white focus-within:border-main">
                {editor && <Toolbar editor={editor} sourceMode={sourceMode} onToggleSource={toggleSource} />}
                {sourceMode ? (
                    <textarea
                        value={sourceText}
                        onChange={handleSourceChange}
                        spellCheck={false}
                        className="block min-h-88 w-full resize-y p-4 font-mono text-sm outline-none"
                    />
                ) : (
                    <EditorContent editor={editor} />
                )}
            </div>

            <aside className="lg:w-60">
                <div className="rounded-lg border border-main/20 bg-white p-3">
                    <p className="text-sm font-medium">빈칸 목록</p>
                    <p className="mt-1 text-xs text-text/60">누르면 커서 위치에 들어가요. 계약 서비스가 신청 값으로 채워요.</p>
                    <div className="mt-3 space-y-2">
                        {FORM_VARIABLES.map((item) => (
                            <button
                                key={item.key}
                                type="button"
                                disabled={sourceMode}
                                onClick={() => insertVariable(item.key)}
                                className="block w-full rounded-lg border border-main/20 px-3 py-1.5 text-left hover:bg-background disabled:opacity-40"
                            >
                                <span className="block text-xs text-text/60">{item.label}</span>
                                <span className="block font-mono text-xs">{`{{${item.key}}}`}</span>
                            </button>
                        ))}
                    </div>
                </div>
            </aside>
        </div>
    )
}

export default RichTextEditor